package net.datacrow.core.migration.itemimport;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.datacrow.console.ComponentFactory;
import net.datacrow.core.DcRepository;
import net.datacrow.core.DcThread;
import net.datacrow.core.data.DataManager;
import net.datacrow.core.modules.DcModule;
import net.datacrow.core.modules.DcModules;
import net.datacrow.core.objects.DcField;
import net.datacrow.core.objects.DcObject;
import net.datacrow.core.resources.DcResources;
import net.datacrow.util.Converter;
import net.datacrow.util.Utilities;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlImporter extends ItemImporter {
    
    private static Logger logger = Logger.getLogger(XmlImporter.class.getName());
    
    public XmlImporter(int moduleIdx, int mode) throws Exception {
        super(moduleIdx, "XML", mode);
    }
    
    @Override
    protected void initialize() {}

    @Override
    public DcThread getTask() {
        return new Task(file, getModule(), client);
    }

    @Override
    public String[] getSupportedFileTypes() {
        return new String[] {"xml"};
    }
    
    @Override
    public void cancel() {}

    @Override
    public String getName() {
        return DcResources.getText("lblSourceReaderType", "XML");
    }
    
    private class Task extends DcThread {
        
        private File file;
        private IItemImporterClient listener;
        private DcModule module;
        
        public Task(File file, DcModule module, IItemImporterClient listener) {
            super(null, "CVS import for " + file);
            this.file = file;
            this.module = module;
            this.listener = listener;
        }
    
        @Override
        public void run() {
            try {
            	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                FileInputStream fis = new FileInputStream(file);
                Document document = db.parse(fis);
                
                Element eTop = document.getDocumentElement();
                
                String name = Converter.getValidXmlTag(module.getSystemObjectName());
                NodeList nlItems = eTop.getElementsByTagName(name);
    
                listener.notifyStarted(nlItems != null ? nlItems.getLength() : 0);
                
                for (int i = 0; !isCanceled() && nlItems != null && i < nlItems.getLength(); i++) {
                	Element eItem = (Element) nlItems.item(i);
                	DcObject dco = module.getDcObject();
                	dco.setIDs();
                	
                	// get the object
                	for (DcField field : module.getFields()) {
    
                		if ((field.isUiOnly() && field.getValueType() != DcRepository.ValueTypes._DCOBJECTCOLLECTION) || 
                			field.getIndex() == DcObject._ID) continue;
                		
                		String fieldName = Converter.getValidXmlTag(field.getSystemName());
                		NodeList nlField = eItem.getElementsByTagName(fieldName);
                		
                		if (nlField == null || nlField.getLength() == 0) continue;
                		
                		Element eField = (Element) nlField.item(0);
                		if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTCOLLECTION) {
                		    // retrieve the items by their module name
                            DcModule referenceMod = DcModules.get(field.getReferenceIdx());
                            String referenceName = Converter.getValidXmlTag(referenceMod.getSystemObjectName());
                			NodeList elReferences = eField.getElementsByTagName(referenceName);
                            for (int j = 0; elReferences != null && j < elReferences.getLength(); j++) {
                                // retrieve the values by the display field index (the system display field index)
                                Element eReference = (Element) elReferences.item(j);
                                DcObject reference = referenceMod.getDcObject();
                                String referenceField = Converter.getValidXmlTag(reference.getField(reference.getSystemDisplayFieldIdx()).getSystemName());
                                NodeList nlRefField = eReference.getElementsByTagName(referenceField);
                                if (nlRefField != null && nlRefField.getLength() > 0) {
                                    Node eRefField = nlRefField.item(0);
                                    DataManager.createReference(dco, field.getIndex(), eRefField.getTextContent());
                                } else {
                                    logger.debug("Could not set value for field " + referenceField + 
                                                 ". The field name does not exist in the XML file");
                                }
                            }
                		} else if (field.getValueType() == DcRepository.ValueTypes._DCOBJECTREFERENCE) {
                            DataManager.createReference(dco, field.getIndex(), eField.getTextContent());
                        } else if (field.getFieldType() == ComponentFactory._RATINGCOMBOBOX) {
                            String rating = eField.getTextContent();
                            if (!Utilities.isEmpty(rating)) {
                                try {
                                    dco.setValue(field.getIndex(), Long.valueOf(rating));
                                } catch (NumberFormatException nfe) {
                                    String sRating = ""; 
                                    for (char c : rating.toCharArray()) {
                                        if (Character.isDigit(c))
                                            sRating += c;
                                        else 
                                            break;
                                    }
                                    dco.setValue(field.getIndex(), Long.valueOf(sRating));
                                }
                            }
                		} else {
    	            		String value = eField.getTextContent();
    	            		if (!Utilities.isEmpty(value))
    	            			setValue(dco, field.getIndex(), value);
                		}
                	}
    
                	listener.notifyProcessed(dco);
                }
             } catch (Exception e) {
            	 logger.error(e, e) ;
             }
        }
    }
}