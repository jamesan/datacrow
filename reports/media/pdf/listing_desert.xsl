﻿<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.1"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format"
	exclude-result-prefixes="fo">
	<xsl:import href="../../_stylesheets/pdf_desert.xsl" />

	<xsl:output method="xml" version="1.0" omit-xml-declaration="no"
		indent="yes" />
	<xsl:param name="versionParam" select="'1.0'" />

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
					<fo:region-body />
					<fo:region-after />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="A4">

				<fo:static-content flow-name="xsl-region-after">
					<fo:block text-align="right" font-size="{$fontSize}">
						page
						<fo:page-number />
					</fo:block>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body">
					<fo:block font-size="{$fontSize}">

						<fo:table table-layout="fixed" width="100%"
							background-color="{$textColor}">

							<fo:table-column column-width="15%" />
							<fo:table-column column-width="30%" />
							<fo:table-column column-width="10%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="15%" />
							<fo:table-column column-width="15%" />

							<fo:table-header>
								<fo:table-row height="{$rowHeight}">
									<fo:table-cell background-color="{$labelColor}"
										border-bottom-style="solid" border-bottom-color="{$borderBottomColor}"
										border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
										padding-left="{$paddingLeft}">
										<fo:block font-weight="bold">Item</fo:block>
									</fo:table-cell>

									<fo:table-cell background-color="{$labelColor}"
										border-bottom-style="solid" border-bottom-color="{$borderBottomColor}"
										border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
										padding-left="{$paddingLeft}">
										<fo:block font-weight="bold">Title</fo:block>
									</fo:table-cell>

									<fo:table-cell background-color="{$labelColor}"
										border-bottom-style="solid" border-bottom-color="{$borderBottomColor}"
										border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
										padding-left="{$paddingLeft}">
										<fo:block font-weight="bold">Year</fo:block>
									</fo:table-cell>

									<fo:table-cell background-color="{$labelColor}"
										border-bottom-style="solid" border-bottom-color="{$borderBottomColor}"
										border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
										padding-left="{$paddingLeft}">
										<fo:block font-weight="bold">Containers</fo:block>
									</fo:table-cell>

									<fo:table-cell background-color="{$labelColor}"
										border-bottom-style="solid" border-bottom-color="{$borderBottomColor}"
										border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
										padding-left="{$paddingLeft}">
										<fo:block font-weight="bold">Rating</fo:block>
									</fo:table-cell>

									<fo:table-cell background-color="{$labelColor}"
										border-bottom-style="solid" border-bottom-color="{$borderBottomColor}"
										border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
										padding-left="{$paddingLeft}">
										<fo:block font-weight="bold">Cover</fo:block>
									</fo:table-cell>
								</fo:table-row>
							</fo:table-header>

							<fo:table-body>
								<xsl:for-each select="data-crow-objects/*">
									<fo:table-row height="{$pictureSizeSmall}">

										<fo:table-cell border-bottom-style="solid"
											border-bottom-color="{$borderBottomColor}"
											border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
											padding-left="{$paddingLeft}">
											<fo:block>
												<xsl:value-of select="item" />
											</fo:block>
										</fo:table-cell>

										<fo:table-cell border-bottom-style="solid"
											border-bottom-color="{$borderBottomColor}"
											border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
											padding-left="{$paddingLeft}">
											<fo:block>
												<xsl:value-of select="title" />
											</fo:block>
										</fo:table-cell>

										<fo:table-cell border-bottom-style="solid"
											border-bottom-color="{$borderBottomColor}"
											border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
											padding-left="{$paddingLeft}">
											<fo:block>
												<xsl:value-of select="year" />
											</fo:block>
										</fo:table-cell>

										<fo:table-cell border-bottom-style="solid"
											border-bottom-color="{$borderBottomColor}"
											border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
											padding-left="{$paddingLeft}">
											<fo:block>
												<xsl:for-each select="container/container">
													<xsl:value-of select="name" />
													<xsl:if test="position()!=last()">
														<xsl:text>,&#160;</xsl:text>
													</xsl:if>
												</xsl:for-each>
											</fo:block>
										</fo:table-cell>

										<fo:table-cell border-bottom-style="solid"
											border-bottom-color="{$borderBottomColor}"
											border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
											padding-left="{$paddingLeft}">
											<fo:block>
												<xsl:value-of select="rating" />
											</fo:block>
										</fo:table-cell>

										<fo:table-cell border-bottom-style="solid"
											border-bottom-color="{$borderBottomColor}"
											border-before-width="{$borderBottomWidth}" padding-top="{$paddingTop}"
											padding-left="{$paddingLeft}">
											<fo:block>
												<xsl:if test="picture-front != ''">
													<fo:external-graphic content-width="{$pictureSizeSmall}"
														content-height="{$pictureSizeSmall}" scaling="uniform">
														<xsl:attribute name="src">url('<xsl:value-of
															select="picture-front" />')</xsl:attribute>
													</fo:external-graphic>
												</xsl:if>
												<xsl:if test="picture-front = ''">
													&#x00A0;
												</xsl:if>
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
