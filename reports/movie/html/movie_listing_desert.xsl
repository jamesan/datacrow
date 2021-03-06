<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>

			<head>
				<link type="text/css" rel="stylesheet" href="desert.css" />
			</head>

			<body>
				<h1>Movies</h1>

				<table cellspacing="0" cellpadding="10">

					<colgroup>
						<col width="200" />
						<col width="50" />
						<col width="100" />
						<col width="100" />
						<col width="75" />
						<col width="75" />
					</colgroup>

					<tr>
						<th>Title</th>
						<th>Year</th>
						<th>Directors</th>
						<th>Playlength</th>
						<th>Rating</th>
						<th>Cover</th>
					</tr>

					<xsl:for-each select="data-crow-objects/movie">
						<tr>
							<td class="listing">
								<xsl:value-of select="title" />
								&#x00A0;
							</td>
							<td class="listing">
								<xsl:value-of select="year" />
								&#x00A0;
							</td>
							<td class="listing">
								<xsl:for-each select="directors/director">
									<xsl:value-of select="name" />
                                    <xsl:if test="position()!=last()">
                                        <xsl:text>, </xsl:text> 
                                    </xsl:if> 									
								</xsl:for-each>
								&#x00A0;
							</td>
							<td class="listing">
								<xsl:value-of select="playlength" />
								&#x00A0;
							</td>
							<td class="listing">
								<xsl:value-of select="rating" />
								&#x00A0;
							</td>
							<td class="listing">
								<xsl:choose>
									<xsl:when test="picture-front != ''">
										<img alt="">
											<xsl:attribute name="src"><xsl:value-of
												select="picture-front" /></xsl:attribute>
										</img>
										&#160;
									</xsl:when>
									<xsl:otherwise>
										&#160;
									</xsl:otherwise>
								</xsl:choose>
							</td>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>

</xsl:stylesheet>