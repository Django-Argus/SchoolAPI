package net.argus.school.api.http.api;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import net.argus.cjson.CJSON;
import net.argus.school.api.Quantities;
import net.argus.school.api.Students;
import net.argus.school.api.http.APIHandler;
import net.argus.school.api.http.pack.PackagePrefab;

public class APIStudentsHandler extends APIHandler {

	public APIStudentsHandler() {
		super("students");
	}
	
	@Override
	public void doGet(HttpExchange exchange) throws IOException {
		send(exchange, PackagePrefab.getStudentsPackage());

	}

	@Override
	public void doPost(HttpExchange exchange) throws IOException {
		CJSON parameters = getCJSONParameters(exchange);
		switch(parameters.getString("action").toLowerCase()) {
			case "get":
				int id = parameters.getInt("id");
				String name = Students.getStudent(id).getString("name");
				send(exchange, PackagePrefab.getStudentPackage(name, id));
				break;
				
			case "add":
				Students.addStudent(parameters.getString("name"));
				sendEmptyPackage(exchange);
				break;
				
			case "update":
				id = parameters.getInt("id");
				String newName = parameters.getString("name");
				boolean success = Students.updateStudentName(id, newName);
				if(success)
					sendEmptyPackage(exchange);
				else
					send500(exchange);
				break;
				
			case "remove":
				id = parameters.getInt("id");
				if(Students.removeStudent(id))
					if(Quantities.removeStudent(id)) {
						sendEmptyPackage(exchange);
						break;
					}
				send500(exchange);
				break;
	
				
		}
	}

}
