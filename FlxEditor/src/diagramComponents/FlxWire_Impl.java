package diagramComponents;


import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;


import diagramSubComponents.BoundCoord;
import diagramSubComponents.Coord;
import diagramSubComponents.FlxConnection;
import javafx.scene.Group;
import javafx.scene.shape.Line;

public class FlxWire_Impl implements FlxWire{
	String wireName;
	boolean debugStatements = false;
	boolean errorStatements = true;

	String parentEffectName;
	FlxConnection src;
	FlxConnection srcWireEnd;
	FlxConnection dest;
	FlxConnection destWireEnd;
	boolean legacy;
	Line wire;
	Group wireGroup;
	double offsetRadius = 6.0;


	public FlxWire_Impl(String wireName)
	{

		try
		{
			this.wireName = wireName;
			if(this.wireName.contains(">"))
			{
				String[] splitWireName = this.wireName.split(">");
				String[] srcParentPort = splitWireName[0].split(":");
				String[] destParentPort = splitWireName[1].split(":");
				this.src = new FlxConnection(srcParentPort[0], srcParentPort[1], 0,0);
				this.dest = new FlxConnection(destParentPort[0], destParentPort[1], 0,0);
			}
			else
			{
				this.src = new FlxConnection(null, null, 0,0);
				this.dest = new FlxConnection(null, null, 0,0);
			}
			this.wire = new Line();
			this.wireGroup = new Group();
			this.wireGroup.setId(this.wireName);
			this.wire.getStyleClass().add("wire");

			this.wire.startXProperty().bind(this.src.getBoundCoordX());
			this.wire.startYProperty().bind(this.src.getBoundCoordY());
			this.wire.endXProperty().bind(this.dest.getBoundCoordX());
			this.wire.endYProperty().bind(this.dest.getBoundCoordY());

			this.wireGroup.getChildren().add(this.wire);
			this.wireGroup.toBack();

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Error creating FlxWire_Impl constructor1");
		}
	}

	public FlxWire_Impl(String wireName, BoundCoord src, BoundCoord dest, String parentEffectName)
	{
		try
		{
			if(parentEffectName.isEmpty())
			{
				System.out.println(wireName + " parentEffect name is empty");
			}
			else
			{
				this.parentEffectName = parentEffectName;
			}
			this.wireName = wireName;
			double xDiff = dest.getX().doubleValue() - src.getX().doubleValue();
			double yDiff = dest.getY().doubleValue() - src.getY().doubleValue();
			double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
			double xEndOffset = xDiff/hyp;
			double yEndOffset =  yDiff/hyp;
			if(this.wireName.contains(">"))
			{
				String[] splitWireName = this.wireName.split(">");
				String[] srcParentPort = splitWireName[0].split(":");
				String[] destParentPort = splitWireName[1].split(":");
				this.src = new FlxConnection(srcParentPort[0], srcParentPort[1],
						src.getX().doubleValue(),src.getY().doubleValue());
				this.dest = new FlxConnection(destParentPort[0], destParentPort[1],
						dest.getX().doubleValue(),dest.getY().doubleValue());
				this.srcWireEnd = new FlxConnection(srcParentPort[0],srcParentPort[1],
						src.getX().doubleValue()+this.offsetRadius*xEndOffset,src.getY().doubleValue()+this.offsetRadius*yEndOffset);
				this.destWireEnd = new FlxConnection(destParentPort[0],destParentPort[1],
						dest.getX().doubleValue()-this.offsetRadius*xEndOffset,dest.getY().doubleValue()-this.offsetRadius*yEndOffset);
			}
			else
			{
				this.src = new FlxConnection(null, null, src.getX().doubleValue(),src.getY().doubleValue());
				this.dest = new FlxConnection(null, null, dest.getX().doubleValue(),dest.getY().doubleValue());
				this.srcWireEnd = new FlxConnection(null,null,
						src.getX().doubleValue()+this.offsetRadius*xEndOffset,src.getY().doubleValue()+this.offsetRadius*yEndOffset);
				this.destWireEnd = new FlxConnection(null,null,
						dest.getX().doubleValue()-this.offsetRadius*xEndOffset,dest.getY().doubleValue()-this.offsetRadius*yEndOffset);
			}

			this.wire = new Line();
			this.wireGroup = new Group();
			this.wireGroup.setId(this.wireName);
			this.wire.getStyleClass().add("wire");

			this.wire.startXProperty().bind(this.srcWireEnd.getBoundCoordX());
			this.wire.startYProperty().bind(this.srcWireEnd.getBoundCoordY());
			this.wire.endXProperty().bind(this.destWireEnd.getBoundCoordX());
			this.wire.endYProperty().bind(this.destWireEnd.getBoundCoordY());

			this.wireGroup.getChildren().add(this.wire);
			this.wireGroup.toBack();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Error creating FlxWire_Impl constructor2");
		}
	}

	public FlxWire_Impl(String wireName, double srcX, double srcY, double destX, double destY, String parentEffectName)
	{
		try
		{
			this.wireName = wireName;
			if(this.wireName.contains(">"))
			{
				String[] splitWireName = this.wireName.split(">");
				String[] srcParentPort = splitWireName[0].split(":");
				String[] destParentPort = splitWireName[1].split(":");
				this.src = new FlxConnection(srcParentPort[0], srcParentPort[1], srcX,srcY);
				this.dest = new FlxConnection(destParentPort[0], destParentPort[1], destX,destY);

			}
			else
			{
				this.src = new FlxConnection(null, null, srcX,srcY);
				this.dest = new FlxConnection(null, null, destX,destY);
			}

			this.wire = new Line();
			this.wireGroup = new Group();
			this.wireGroup.setId(this.wireName);
			this.wire.getStyleClass().add("wire");


			this.wire.startXProperty().bind(this.src.getBoundCoordX());
			this.wire.startYProperty().bind(this.src.getBoundCoordY());
			this.wire.endXProperty().bind(this.dest.getBoundCoordX());
			this.wire.endYProperty().bind(this.dest.getBoundCoordY());


			this.wireGroup.getChildren().add(this.wire);
			this.wireGroup.toBack();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Error creating FlxWire_Impl constructor3");
		}
	}

	// use for taking in data from combo files
	public FlxWire_Impl(JsonValue processConnectionJson, String parentEffectName)
	{
		try
		{
			if(parentEffectName.isEmpty())
			{
				System.out.println("wire parentEffect name is empty");
			}
			else
			{
				this.parentEffectName = parentEffectName;
			}

			JsonObject processConnectionJsonObject = (JsonObject)processConnectionJson;
			String srcObject=null, srcPort=null, destObject=null, destPort=null;
			double srcX=0, srcY=0, destX=0, destY=0;
			if(processConnectionJsonObject.containsKey("srcPort"))
			{
				// is src Parent effect or process?
				this.legacy = true;
				if(processConnectionJsonObject.containsKey("srcProcess"))
					srcObject = processConnectionJsonObject.get("srcProcess").toString().replace("\"", "");
				else srcObject = processConnectionJsonObject.get("srcEffect").toString().replace("\"", "");

				srcX = Double.parseDouble(processConnectionJsonObject.get("x1").toString().replace("\"", ""));
				srcY = Double.parseDouble(processConnectionJsonObject.get("y1").toString().replace("\"", ""));

				// is dest Parent effect or process?
				if(processConnectionJsonObject.containsKey("destProcess"))
					destObject = processConnectionJsonObject.getString("destProcess").toString().replace("\"", "");
				else destObject = processConnectionJsonObject.getString("destEffect").toString().replace("\"", "");

				destX = Double.parseDouble(processConnectionJsonObject.get("x2").toString().replace("\"", ""));
				destY = Double.parseDouble(processConnectionJsonObject.get("y2").toString().replace("\"", ""));

				double xDiff = destX - srcX;
				double yDiff = destY - srcY;
				double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
				double xEndOffset = xDiff/hyp;
				double yEndOffset =  yDiff/hyp;


				this.src = new FlxConnection(srcObject,processConnectionJsonObject.get("srcPort").toString().replace("\"", ""),
						srcX,srcY);
				this.dest = new FlxConnection(destObject,processConnectionJsonObject.get("destPort").toString().replace("\"", ""),
						destX,destY);
				this.srcWireEnd = new FlxConnection(srcObject,processConnectionJsonObject.get("srcPort").toString().replace("\"", ""),
						srcX+this.offsetRadius*xEndOffset,srcY+this.offsetRadius*yEndOffset);
				this.destWireEnd = new FlxConnection(destObject,processConnectionJsonObject.get("destPort").toString().replace("\"", ""),
						destX-this.offsetRadius*xEndOffset,destY-this.offsetRadius*yEndOffset);

			}
			else if(processConnectionJsonObject.containsKey("src"))
			{
				// create source connection
				JsonObject srcJsonObject = (JsonObject)processConnectionJsonObject.get("src");
				this.legacy = false;
				if(srcJsonObject.containsKey("control")) // wire is connecting control and process parameter
				{
					srcObject = srcJsonObject.get("control").toString().replace("\"", "");
					srcPort = srcJsonObject.get("output").toString().replace("\"", "");
				}
				else if(srcJsonObject.containsKey("process"))// wire is connecting src process output to dest process input
				{
					srcObject = srcJsonObject.get("process").toString().replace("\"", "");
					srcPort = srcJsonObject.get("output").toString().replace("\"", "");
				}
				else if(srcJsonObject.containsKey("object"))
				{
					srcObject = srcJsonObject.get("object").toString().replace("\"", "");
					srcPort = srcJsonObject.get("port").toString().replace("\"", "");

				}
				srcX = Double.parseDouble(srcJsonObject.get("x").toString().replace("\"", ""));
				srcY = Double.parseDouble(srcJsonObject.get("y").toString().replace("\"", ""));

				// create dest connection
				JsonObject destJsonObject = (JsonObject)processConnectionJsonObject.get("dest");

				if(srcJsonObject.containsKey("control")) // wire is connecting control and process parameter
				{
					destObject = destJsonObject.get("process").toString().replace("\"", "");
					destPort = destJsonObject.get("parameter").toString().replace("\"", "");
				}
				else if(srcJsonObject.containsKey("process"))// wire is connecting src process output to dest process input
				{
					destObject = destJsonObject.get("process").toString().replace("\"", "");
					destPort = destJsonObject.get("input").toString().replace("\"", "");
				}
				else if(srcJsonObject.containsKey("object"))
				{
					destObject = destJsonObject.get("object").toString().replace("\"", "");
					destPort = destJsonObject.get("port").toString().replace("\"", "");
				}

				destX = Double.parseDouble(destJsonObject.get("x").toString().replace("\"", ""));
				destY = Double.parseDouble(destJsonObject.get("y").toString().replace("\"", ""));

				double xDiff = destX - srcX;
				double yDiff = destY - srcY;
				double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
				double xEndOffset = xDiff/hyp;
				double yEndOffset =  yDiff/hyp;

				this.src = new FlxConnection(srcObject, srcPort, srcX, srcY);
				this.srcWireEnd = new FlxConnection(srcObject, srcPort, srcX+this.offsetRadius*xEndOffset,
						srcY+this.offsetRadius*yEndOffset);

				this.dest = new FlxConnection(destObject, destPort, destX, destY);
				this.destWireEnd = new FlxConnection(destObject, destPort, destX-this.offsetRadius*xEndOffset,
						destY-this.offsetRadius*yEndOffset);
			}

			this.wireName = this.src.getName() + ">" + this.dest.getName();
			this.wire = new Line();
			this.wireGroup = new Group();
			this.wireGroup.setId(this.wireName);
			this.wire.getStyleClass().add("wire");

			this.wire.toBack();

			this.wire.startXProperty().bind(this.srcWireEnd.getBoundCoordX());
			this.wire.startYProperty().bind(this.srcWireEnd.getBoundCoordY());
			this.wire.endXProperty().bind(this.destWireEnd.getBoundCoordX());
			this.wire.endYProperty().bind(this.destWireEnd.getBoundCoordY());

			this.wireGroup.getChildren().add(this.wire);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Error creating FlxWire_Impl constructor4");
		}
	}


	public FlxWire_Impl(BoundCoord src, BoundCoord dest) // used for tempWire in FlxDrawingArea_Impl
	{
		try
		{

			this.src = new FlxConnection(null, null,src.getX().doubleValue(),src.getY().doubleValue());
			this.dest = new FlxConnection(null, null,dest.getX().doubleValue(),dest.getY().doubleValue());
			this.srcWireEnd = new FlxConnection(null,null,src.getX().doubleValue(),src.getY().doubleValue());
			this.destWireEnd = new FlxConnection(null,null,dest.getX().doubleValue(),dest.getY().doubleValue());


			this.wire = new Line();
			this.wireGroup = new Group();
			this.wireGroup.setId(this.wireName);
			this.wire.getStyleClass().add("wire");

			this.wire.startXProperty().bind(this.srcWireEnd.getBoundCoordX());
			this.wire.startYProperty().bind(this.srcWireEnd.getBoundCoordY());
			this.wire.endXProperty().bind(this.destWireEnd.getBoundCoordX());
			this.wire.endYProperty().bind(this.destWireEnd.getBoundCoordY());

			this.wireGroup.getChildren().add(this.wire);
			this.wireGroup.toBack();
		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("Error creating FlxWire_Impl constructor2");
		}
	}







	public void setWireName(String wireName)
	{
		this.wireName = wireName;
	}

	public void setParentEffect(String parentEffectName)
	{
		this.parentEffectName = parentEffectName;

		if(this.src.getParentObject().contains("("))
		{
			this.src.setParentObjectName("("+ parentEffectName + ")");
		}

		if(this.dest.getParentObject().contains("("))
		{
			this.dest.setParentObjectName("("+ parentEffectName + ")");
		}
	}

	public String getWireName()
	{
		return this.wireName;
	}

	public String getParentEffect()
	{
		return this.parentEffectName;
	}

	public void setSourceConnection(Coord srcConn)
	{
		try
		{
			this.src.setCoordX(srcConn.getX());
			this.src.setCoordY(srcConn.getY());

			double xDiff = this.dest.getCoordX() - this.src.getCoordX();
			double yDiff = this.dest.getCoordY() - this.src.getCoordY();
			double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
			double xEndOffset = xDiff/hyp;
			double yEndOffset =  yDiff/hyp;

			this.srcWireEnd.setCoordX(this.src.getCoordX()+this.offsetRadius*xEndOffset);
			this.srcWireEnd.setCoordY(this.src.getCoordY()+this.offsetRadius*yEndOffset);
			this.destWireEnd.setCoordX(this.dest.getCoordX()-this.offsetRadius*xEndOffset);
			this.destWireEnd.setCoordY(this.dest.getCoordY()-this.offsetRadius*yEndOffset);

		}
		catch(Exception e)
		{
			System.out.println("FlxWire_Impl::setSourceConnection: " + e);
		}

	}

	public void setSourceConnection(BoundCoord srcConn)
	{
		try
		{
			this.src.setBoundCoordX(srcConn.getX());
			this.src.setBoundCoordY(srcConn.getY());

			double xDiff = this.dest.getCoordX() - this.src.getCoordX();
			double yDiff = this.dest.getCoordY() - this.src.getCoordY();
			double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
			double xEndOffset = 0;
			double yEndOffset =  0;

			if(hyp > 0)
			{
				xEndOffset = xDiff/hyp;
				yEndOffset =  yDiff/hyp;
			}

			this.srcWireEnd.setCoordX(this.src.getCoordX()+this.offsetRadius*xEndOffset);
			this.srcWireEnd.setCoordY(this.src.getCoordY()+this.offsetRadius*yEndOffset);
			this.destWireEnd.setCoordX(this.dest.getCoordX()-this.offsetRadius*xEndOffset);
			this.destWireEnd.setCoordY(this.dest.getCoordY()-this.offsetRadius*yEndOffset);
		}
		catch(Exception e)
		{
			System.out.println("FlxWire_Impl::setSourceConnection: " + e);
		}
	}

	public void setSourceConnection(double x, double y)
	{
		this.src.setDoubleCoord(x,y);
	}


	public void setDestConnection(Coord destConn)
	{
		try
		{
			this.dest.setCoordX(destConn.getX());
			this.dest.setCoordY(destConn.getY());

			double xDiff = this.dest.getCoordX() - this.src.getCoordX();
			double yDiff = this.dest.getCoordY() - this.src.getCoordY();
			double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
			double xEndOffset = xDiff/hyp;
			double yEndOffset =  yDiff/hyp;
			this.srcWireEnd.setCoordX(this.src.getCoordX()+this.offsetRadius*xEndOffset);
			this.srcWireEnd.setCoordY(this.src.getCoordY()+this.offsetRadius*yEndOffset);
			this.destWireEnd.setCoordX(this.dest.getCoordX()-this.offsetRadius*xEndOffset);
			this.destWireEnd.setCoordY(this.dest.getCoordY()-this.offsetRadius*yEndOffset);

		}
		catch(Exception e)
		{
			System.out.println("FlxWire_Impl::setDestConnection: " + e);
		}

	}

	public void setDestConnection(BoundCoord destConn)
	{

		try
		{
			this.dest.setBoundCoordX(destConn.getX());
			this.dest.setBoundCoordY(destConn.getY());

			double xDiff = this.dest.getCoordX() - this.src.getCoordX();
			double yDiff = this.dest.getCoordY() - this.src.getCoordY();
			double hyp = Math.sqrt(xDiff*xDiff + yDiff*yDiff);
			double xEndOffset = xDiff/hyp;
			double yEndOffset =  yDiff/hyp;
			this.srcWireEnd.setCoordX(this.src.getCoordX()+this.offsetRadius*xEndOffset);
			this.srcWireEnd.setCoordY(this.src.getCoordY()+this.offsetRadius*yEndOffset);
			this.destWireEnd.setCoordX(this.dest.getCoordX()-this.offsetRadius*xEndOffset);
			this.destWireEnd.setCoordY(this.dest.getCoordY()-this.offsetRadius*yEndOffset);
		}
		catch(Exception e)
		{
			System.out.println("FlxWire::setDestConnection error: " + e);
		}
	}

	public void setDestConnection(double x, double y)
	{
		this.dest.setDoubleCoord(x,y);
	}

	public BoundCoord getSourceConnection()
	{
		return this.src.getBoundCoord();
	}

	public BoundCoord getDestConnection()
	{
		return this.dest.getBoundCoord();
	}

	public Group getWire()
	{
		return this.wireGroup;
	}

	public void deleteWireSymbol()
	{
		this.wireGroup.getChildren().clear();
	}

	public JsonValue getProcessConnectionDataLegacy() {

		JsonObjectBuilder processConnectionData = Json.createObjectBuilder();

		String[] wireConnectionNames = this.wireName.split(">");
		String srcPort = wireConnectionNames[0].split(":")[0];
		String srcProcEffect = wireConnectionNames[0].split(":")[0];
		String destPort = wireConnectionNames[1].split(":")[0];
		String destProcEffect = wireConnectionNames[1].split(":")[1];

		processConnectionData.add("parentEffect", this.parentEffectName);
		processConnectionData.add("srcPort", srcPort);
		if(srcProcEffect.indexOf("(") >= 0)
		{
			processConnectionData.add("srcEffect", srcProcEffect);
		}
		else
		{
			processConnectionData.add("srcProcess", srcProcEffect);
		}
		processConnectionData.add("x1", this.src.getCoordX());
		processConnectionData.add("y1", this.src.getCoordY());

		processConnectionData.add("destPort", destPort);
		if(srcProcEffect.indexOf("(") >= 0)
		{
			processConnectionData.add("destEffect", destProcEffect);
		}
		else
		{
			processConnectionData.add("destProcess", destProcEffect);
		}
		processConnectionData.add("x2", this.dest.getCoordX());
		processConnectionData.add("y2", this.dest.getCoordY());
		processConnectionData.build();

		return (JsonValue)processConnectionData;
	}

	public JsonValue getProcessConnectionData() {
		if(this.debugStatements) System.out.println("getting process connection data: " + this.wireName);
		JsonObjectBuilder processConnectionData = Json.createObjectBuilder();
		JsonObject processConnectionDataBuilt = null;

		try
		{
			processConnectionData.add("parentEffect", this.parentEffectName);

			processConnectionData.add("src", this.src.getConnectionData());
			processConnectionData.add("dest", this.dest.getConnectionData());
			processConnectionDataBuilt = processConnectionData.build();

			if(this.debugStatements) System.out.println("FlxWire data for " + this.wireName + ": " + processConnectionDataBuilt.toString());

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxWire::getProcessConnectionData error: " + e);
		}
		if(this.debugStatements) System.out.println("process connection data retrieved: " + this.wireName);
		return (JsonValue)processConnectionDataBuilt;
	}

	public String getProcessConnectionString() {
		if(this.debugStatements) System.out.println("getting process connection data: " + this.wireName);
		String processConnectionString = "";

		try
		{
			processConnectionString += "{\"parentEffect\":\"" + this.getParentEffect() + "\",";
			processConnectionString += "\"src\":" + this.src.getConnectionString() + ",";
			processConnectionString += "\"dest\":" + this.dest.getConnectionString() + "}";

			if(this.debugStatements) System.out.println("FlxWire data for " + this.wireName + ": " + processConnectionString);

		}
		catch(Exception e)
		{
			if(this.errorStatements) System.out.println("FlxWire::getProcessConnectionData error: " + e);
		}

		if(this.debugStatements) System.out.println("process connection data : " + processConnectionString);
		return processConnectionString;
	}




	public boolean isWireLegacy()
	{
		return this.legacy;
	}
}
