package net.argus.school.api;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import net.argus.cjson.Array;
import net.argus.cjson.CJSON;
import net.argus.cjson.CJSONItem;
import net.argus.cjson.CJSONParser;
import net.argus.cjson.value.CJSONInteger;
import net.argus.cjson.value.CJSONObject;
import net.argus.cjson.value.CJSONString;
import net.argus.cjson.value.CJSONValue;
import net.argus.file.CJSONFile;
import net.argus.instance.Instance;

public class Materials {

	public static final CJSONFile FILE = new CJSONFile("materials", "", Instance.SYSTEM);
	public static final CJSON MATERIALS = CJSONParser.getCJSON(FILE);
	
	public static final IdRegister ID_REGISTER = new IdRegister(MATERIALS.getArray("materials"));

	
	public static CJSONValue getMATERIALSArray() {
		return MATERIALS.getValue("materials");
	}
	
	public synchronized static void addMaterials(String name, int baseQuantity) throws IOException {
		int id = ID_REGISTER.genId();
		Quantities.addMaterial(id, baseQuantity);
		
		CJSONObject obj = new CJSONObject();

		obj.addItem("name", new CJSONString(name));
		obj.addItem("id", new CJSONInteger(id));
		
		MATERIALS.getArray("materials").addValue(obj);

		writeFile();
	}
	
	public synchronized static Material getMaterial(int id) throws IOException {
		Array array = MATERIALS.getArray("materials");
		for(CJSONValue val : array.getArray()) {
			CJSONObject obj = (CJSONObject) val;
			if(obj.getInt("id") == id)
				return new Material(obj.getString("name"), id);
		}
		return null;
	}
	
	public synchronized static boolean updateMaterialsName(int id, String name) throws IOException {
		Array array = MATERIALS.getArray("materials");
		for(CJSONValue val : array.getArray()) {
			CJSONObject obj = (CJSONObject) val;
			if(obj.getInt("id") == id) {
				List<CJSONItem> items = obj.getValue();
				int index = -1;
				for(CJSONItem item : items) {
					index ++;
					if(item.getName().equals("name"))
						break;
				}
				items.set(index, new CJSONItem("name", new CJSONString(name)));
				
				writeFile();
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean removeMaterial(int id) throws IOException {
		Array array = MATERIALS.getArray("materials");
		for(CJSONValue val : array.getArray()) {
			CJSONObject obj = (CJSONObject) val;
			if(obj.getInt("id") == id) {
				List<CJSONValue> vals = array.getArray();
				vals.remove(val);
				array.setArray(vals);
				
				ID_REGISTER.removeId(id);
				
				writeFile();
				return true;
			}
		}
		return false;
	}
	
	private static void writeFile() throws IOException {
        FileOutputStream fos = new FileOutputStream(FILE.getFile());
        
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        
        osw.write(MATERIALS.toString());
        
        osw.flush();
        osw.close();
	}

}
