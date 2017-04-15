package org.king.apps.lunchvote.services;


import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.king.apps.lunchvote.controllers.RoomController;
import org.king.apps.lunchvote.exception.RoomNotFoundException;
import org.king.apps.lunchvote.models.Room;
import org.king.apps.lunchvote.models.User;
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
		
		try {
			Room room = roomCtrl.getRoom(id);
			return Response.ok(room).build();
		} catch(RoomNotFoundException e) {
			return Response.status(Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
	
	@GET
	@Path("/{id}/join")
	public Response validateUser(@PathParam("id") String id, @Context HttpServletRequest req) {
		System.out.println("Join Room: "+id);
		
		try {
			User user = roomCtrl.addUserToRoom(id, req.getRemoteAddr());
			return Response.ok(user).build();
		} catch(RoomNotFoundException e) {
			return Response.status(Status.NOT_FOUND).entity(e).build();
		}
	}
	
	@GET
	@Path("/keys")
	public Response getKeys() {
		return Response.ok(roomCtrl.getRoomKeys()).build();
	}

}
