  <?xml version="1.0" encoding="ISO-8859-1" standalone="no"?>
  <!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN" "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
  <helpset version="1.0">
    <title>Title</title>
    <maps>
      <homeID>top</homeID>
      <mapref location="jHelpMap.jhm"/>
    </maps>
    <view>
      <name>TOC</name>
      <label>Table Of Contents</label>
      <type>javax.help.TOCView</type>
      <data>jHelpToc.xml</data>
    </view>
    <view>
      <name>Search</name>
      <label>Search</label>
      <type>javax.help.SearchView</type>
      <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
    </view>
</helpset>