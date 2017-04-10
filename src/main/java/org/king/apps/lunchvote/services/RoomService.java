package org.king.apps.lunchvote.services;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.king.apps.lunchvote.controllers.RoomController;
import org.king.apps.lunchvote.models.Room;
import org.springframework.stereotype.Service;

@Service("RoomService")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Path("/room")
@Consumes("application/json")
@Produces("application/json")
public class RoomService {
	
	private RoomController roomCtrl;
	
	public RoomService() {
		roomCtrl = new RoomController();
	}
	
	@GET
	public Response getRooms() {
		return Response.ok(roomCtrl.getAllRooms()).build();
	}
	
	@POST
	public Response createRoom(@Context UriInfo uriInfo, Room room) {
		String id = roomCtrl.createRoom(room);
		
		UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        builder.path(id);
        return Response.created(builder.build()).entity("{\"roomId\": \""+id+"\"}").build();
		
	}
	
	@GET
	@Path("/{id}")
	public Response getRoom(@PathParam("id") String id) {
		System.out.println("Path ID: "+id);
		Room room = roomCtrl.getRoom(id);
		
		return Response.ok(room).build();
	}
	
	@GET
	@Path("/keys")
	public Response getKeys() {
		return Response.ok(roomCtrl.getRoomKeys()).build();
	}

}
