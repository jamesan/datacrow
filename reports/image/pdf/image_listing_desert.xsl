﻿<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
<xsl:import href="../../_stylesheets/pdf_desert.xsl" />

  <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
  <xsl:param name="versionParam" select="'1.0'"/> 

  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin-top="2cm" margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
          <fo:region-body/>
          <fo:region-after />
        </fo:simple-page-master>
      </fo:layout-master-set>
           
      <fo:page-sequence master-reference="A4">

        <fo:static-content flow-name="xsl-region-after">
          <fo:block text-align="right" font-size="10pt">page <fo:page-number/></fo:block>
        </fo:static-content>
        
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-size="{$fontSize}">

            <fo:table table-layout="fixed" width="100%" background-color="{$textColor}">

              <fo:table-column column-width="30%" /> 
              <fo:table-column column-width="10%" />
              <fo:table-column column-width="15%" />
              <fo:table-column column-width="15%" />
              <fo:table-column column-width="15%" />
              <fo:table-column column-width="15%" />

              <fo:table-header>
                <fo:table-row height="{$rowHeight}">
                  <fo:table-cell background-color="{$labelColor}" border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                    <fo:block font-weight="bold">Title</fo:block>
                  </fo:table-cell>
                        
                  <fo:table-cell background-color="{$labelColor}" border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                    <fo:block font-weight="bold">Date</fo:block>
                  </fo:table-cell>

                  <fo:table-cell background-color="{$labelColor}" border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                    <fo:block font-weight="bold">State</fo:block>
                  </fo:table-cell>
                        
                  <fo:table-cell background-color="{$labelColor}" border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                    <fo:block font-weight="bold">Dimension</fo:block>
                  </fo:table-cell>
                        
                  <fo:table-cell background-color="{$labelColor}" border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                    <fo:block font-weight="bold">Rating</fo:block>
                  </fo:table-cell>
                        
                  <fo:table-cell background-color="{$labelColor}" border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                    <fo:block font-weight="bold">Image</fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </fo:table-header>

              <fo:table-body>
                <xsl:for-each select="data-crow-objects/image"> 
                  <fo:table-row height="{$pictureSizeSmall}">
                    <fo:table-cell border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                      <fo:block><xsl:value-of select="title"/></fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                      <fo:block><xsl:value-of select="date"/></fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                      <fo:block><xsl:value-of select="state"/></fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                      <fo:block><xsl:value-of select="width"/>*<xsl:value-of select="height"/></fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                      <fo:block><xsl:value-of select="rating"/></fo:block>
                    </fo:table-cell>

                    <fo:table-cell border-bottom-style="solid" border-bottom-color="{$borderBottomColor}" border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}" padding-left="{$paddingLeft}">
                      <fo:block>
                        <xsl:if test="image != ''">
                          <fo:external-graphic content-width="{$pictureSizeSmall}" content-height="{$pictureSizeSmall}" scaling="uniform">
                            <xsl:attribute name="src">url('<xsl:value-of select="image" />')</xsl:attribute>
                          </fo:external-graphic> 
                        </xsl:if>
                        <xsl:if test="image = ''">&#x00A0;</xsl:if>
                      </fo:block>
                    </fo:table-cell>
                  </fo:table-row>
                </xsl:for-each>
              </fo:table-body>
            </fo:table>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:stylesheet>
