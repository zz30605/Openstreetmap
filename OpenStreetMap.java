import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class OpenStreetMap {
	static String s = null;

	static class MySAXHandler extends DefaultHandler {

		String type = "motorway,trunk,primary,secondary,tertiary,unclassified,residential,service";
		HashMap<String, double[]> nodes = new HashMap<>();
		int count = 0;
		boolean highwayFlag = false;
		FileOutputStream fosPoint;
		FileOutputStream fosArc;
		OutputStreamWriter oswPoint;
		OutputStreamWriter oswArc;
		BufferedWriter bwPoint;
		BufferedWriter bwArc;

		public void startDocument() throws SAXException {

			// File pointFile = new
			// File("/Users/wangqin/Downloads/map/Point.txt");
			// File arcFile = new File("/Users/wangqin/Downloads/map/Arc.txt");
			File pointFile = new File(
					"C:/Users/Brock/Downloads/Downloads/Point.txt");
			File arcFile = new File(
					"C:/Users/Brock/Downloads/Downloads/Arc.txt");
			try {
				fosPoint = new FileOutputStream(pointFile);
				fosArc = new FileOutputStream(arcFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			oswPoint = new OutputStreamWriter(fosPoint);
			oswArc = new OutputStreamWriter(fosArc);
			bwPoint = new BufferedWriter(oswPoint);
			bwArc = new BufferedWriter(oswArc);
			System.out.println("~Document Start~");
		}

		public void endDocument() throws SAXException {
			try {
				bwPoint.close();
				oswPoint.close();
				fosPoint.close();
				bwArc.close();
				oswArc.close();
				fosArc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("~Document End~");
		}

		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ((attributes != null) && attributes.getLength() > 0) {
				if (qName.equals("node")) {
					StringBuilder sb = new StringBuilder();
					sb.append(attributes.getValue("id") + ",");
					sb.append(attributes.getValue("lat") + ",");
					sb.append(attributes.getValue("lon"));
					double[] xy = {
							Double.parseDouble(attributes.getValue("lon")),
							Double.parseDouble(attributes.getValue("lat")) };
					nodes.put(attributes.getValue("id"), xy);
					try {
						bwPoint.write(sb.toString() + "\r\n");
						bwPoint.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(sb.toString());
				} else if (qName.equals("way")) {
					if (s != null) {
						try {
							if (highwayFlag == false) {
								bwArc.write("0\r\n");
								System.out.println(s + "not target");
							}
							bwArc.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						s = null;
					}
					s = attributes.getValue("id") + ","
							+ attributes.getValue("version") + ",";
					highwayFlag = false;
				} else if (qName.equals("nd")) {
					if (s == null)
						return;
					String temp = s + attributes.getValue("ref") + ","
							+ nodes.get(attributes.getValue("ref"))[0] + ","
							+ nodes.get(attributes.getValue("ref"))[1];
					try {
						bwArc.write(temp + "\r\n");
						bwArc.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(temp);
				} else if (qName.equals("tag")) {
					if (s == null)
						return;
					if (attributes.getValue("k").equals("highway")) {
						try {
							if (type.contains(attributes.getValue("v"))) {
								bwArc.write("1\r\n");
								count++;
								highwayFlag = true;
								System.out
										.println(s + attributes.getValue("v"));

							}
							bwArc.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else if (qName.equals("relation")) {
					if (s != null) {
						try {
							if (highwayFlag == false) {
								bwArc.write("0\r\n");
								System.out.println(s + "not target");
							}
							bwArc.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						s = null;
						System.out.println(count);
					}
				}
			}
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {

		}

		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// System.out.print(new String(ch, start, length));
		}
	}

	public static void main(String[] args) {
		SAXParserFactory saxfac = SAXParserFactory.newInstance();
		try {
			SAXParser saxparser = saxfac.newSAXParser();
			// InputStream is = new
			// FileInputStream("/Users/wangqin/Downloads/map/beijing_china.osm");
			InputStream is = new FileInputStream(
					"C:/Users/Brock/Downloads/Downloads/beijing_china.osm");
			MySAXHandler handler = new MySAXHandler();
			saxparser.parse(is, handler);
			;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}