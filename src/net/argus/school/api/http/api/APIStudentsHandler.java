package net.argus.school.api.http.api;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

import net.argus.cjson.CJSON;
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
			case "add":
				Students.addStudent(parameters.getString("name"));
				sendEmptyPackage(exchange);
				break;
				
			case "remove":
				Students.removeStudent(parameters.getInt("uid"));
				sendEmptyPackage(exchange);
				break;
		}
	}

}
