package diagramSubComponents;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.SVGPath;

class PolygonCoords {
	double[] x = new double[5];
	double[] y = new double[5];
	int coordCount;
}



public class FlxSymbol {
	String process;
	String color;
	JsonValue labels;
	JsonValue bodyJsonValue;
	JsonValue graphicJsonValue;
	boolean errorStatements = true;

	Group symbol = new Group();
	Group symbolContainer = new Group();
	PolygonCoords bodyCoords = new PolygonCoords();
	List<String> wireData = new ArrayList<String>();
	List<String> graphicData = new ArrayList<String>();

	Polygon body;
	List<SVGPath> graphics = new ArrayList<SVGPath>();
	double[] polygonCoords = new double[10];
	double x;
	double y;

	public FlxSymbol(String process, JsonValue body, JsonValue graphic, String color, double x, double y)
	{

		this.process = process;
		this.color = color;
		this.bodyJsonValue = body;
		this.graphicJsonValue = graphic;

		JsonArray symbolSvgPaths = (JsonArray)body;

		for(int i = 0; i < symbolSvgPaths.size(); i++)
		{
			if(symbolSvgPaths.get(i).toString().indexOf('M') >= 0)
			{
				String svgPathString = new String(symbolSvgPaths.getString(i)+" z");
				this.wireData.add(svgPathString);
			}
			else
			{
				try
				{
					String[] coordStringArray  = symbolSvgPaths.get(i).toString().split(" ");
					for(int coordStringArrayIndex = 0; coordStringArrayIndex < 5; coordStringArrayIndex++)
					{
						String cleanCoordString = coordStringArray[coordStringArrayIndex].replace("\"", "");
						String[] coordString = cleanCoordString.split(",");

						this.polygonCoords[2*coordStringArrayIndex] = Double.parseDouble(coordString[0]);
						this.polygonCoords[2*coordStringArrayIndex+1] = Double.parseDouble(coordString[1]);
					}
				}
				catch(Exception e)
				{
					if(this.errorStatements) System.out.println(e);
				}
			}
		}

		JsonArray symbolSvgGraphicPaths = (JsonArray)graphic;

		for(int i = 0; i < symbolSvgGraphicPaths.size(); i++)
		{
			this.graphicData.add(new String(symbolSvgGraphicPaths.getString(i)));
		}

		this.x = x;
		this.y = y;

		this.body = new Polygon(polygonCoords);
		if(this.color.indexOf("yellow")>=0)
			this.body.setFill(Color.YELLOW);
		else if(this.color.indexOf("blue")>=0)
			this.body.setFill(Color.BLUE);
		this.body.setStroke(Color.BLACK);

		this.symbol.getChildren().add(this.body);

        for(int i = 0; i < this.graphicData.size(); i++)
        {
            SVGPath svg = new SVGPath();
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(1.2);
            svg.setFill(Color.TRANSPARENT);
            svg.setContent(this.graphicData.get(i));

            this.symbol.getChildren().add(svg);
        }

        for(int i = 0; i < this.wireData.size(); i++)
        {
            SVGPath svg = new SVGPath();
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(3.0);
            svg.setFill(Color.TRANSPARENT);
            svg.setContent(this.wireData.get(i));
            this.symbol.getChildren().add(svg);
        }
		this.color = color;

	}


	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	public Group getSymbol(double x, double y)
	{
		this.x = x;
		this.y = y;
		this.symbol.setTranslateX(this.x);
		this.symbol.setTranslateY(this.y);

        return this.symbol;
	}

	public Group getSymbol()
	{

        return this.symbol;
	}

	public Coord getSymbolCoords()
	{
		Coord symbolCoord = new Coord(this.x, this.y);

		return symbolCoord;
	}

	public void deleteSymbol()
	{

	}

	public void setSelectIndicator(boolean selected)  // if process or control is selected, black lines will widen
	{
		double strokeWidth = 0.0;
		if(selected) strokeWidth = 3.0;
		else strokeWidth = 1.2;

		this.symbol.getChildren().clear();
		this.body = new Polygon(polygonCoords);
		this.body.setStrokeWidth(strokeWidth);
		if(this.color.indexOf("yellow")>=0)
			this.body.setFill(Color.YELLOW);
		else if(this.color.indexOf("blue")>=0)
			this.body.setFill(Color.BLUE);
		this.body.setStroke(Color.BLACK);
		this.symbol.getChildren().add(this.body);

        for(int i = 0; i < this.graphicData.size(); i++)
        {
            SVGPath svg = new SVGPath();
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(strokeWidth);
            svg.setFill(Color.TRANSPARENT);
            svg.setContent(this.graphicData.get(i));

            this.symbol.getChildren().add(svg);
        }

        for(int i = 0; i < this.wireData.size(); i++)
        {
            SVGPath svg = new SVGPath();
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(3.0);
            svg.setFill(Color.TRANSPARENT);
            svg.setContent(this.wireData.get(i));
            this.symbol.getChildren().add(svg);
        }
	}

	public void flipProcess(boolean selected, boolean flipped) { // toggles process block forward or backward (feedback)

		double scale = 1.0;

		if(flipped == true)
		{
			scale = 1.0;
			flipped = false;
		}
		else
		{
			scale = -1.0;
			flipped = true;
		}

		double strokeWidth = 0.0;
		if(selected) strokeWidth = 3.0;
		else strokeWidth = 1.2;

		this.symbol.getChildren().clear();
		this.body = new Polygon(polygonCoords);
		this.body.setStrokeWidth(strokeWidth);
		if(this.color.indexOf("yellow")>=0)
			this.body.setFill(Color.YELLOW);
		else if(this.color.indexOf("blue")>=0)
			this.body.setFill(Color.BLUE);
		this.body.setStroke(Color.BLACK);
		this.symbol.getChildren().add(this.body);

        for(int i = 0; i < this.graphicData.size(); i++)
        {
            SVGPath svg = new SVGPath();
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(strokeWidth);
            svg.setFill(Color.TRANSPARENT);
            svg.setContent(this.graphicData.get(i));
            this.symbol.getChildren().add(svg);
        }

        for(int i = 0; i < this.wireData.size(); i++)
        {
            SVGPath svg = new SVGPath();
            svg.setStroke(Color.BLACK);
            svg.setStrokeWidth(3.0);
            svg.setFill(Color.TRANSPARENT);
            svg.setContent(this.wireData.get(i));
            this.symbol.getChildren().add(svg);
        }

        this.symbol.setScaleX(scale);

	}

	public JsonValue getSymbolData()
	{
		JsonObject locationData = Json.createObjectBuilder()
				.add("x", this.x).add("y", this.y).build();

		JsonObject symbolData = Json.createObjectBuilder()
				.add("location", locationData)
				.add("body", this.bodyJsonValue)
				.add("graphic", this.graphicJsonValue)
				.add("color",this.color)
				.build();

		return symbolData;
	}
}
