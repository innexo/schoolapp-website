package innexo;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
public class InnexoApiController{

  @Autowired
  UserService userService;

  @Autowired
  NotificationService notificationService;

  @Autowired
  PostService postService;

  static final ResponseEntity<?> BAD_REQUEST = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  static final ResponseEntity<?> INTERNAL_SERVER_ERROR = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  static final ResponseEntity<?> OK = new ResponseEntity<>(HttpStatus.OK);
  static final ResponseEntity<?> NOT_FOUND = new ResponseEntity<>(HttpStatus.NOT_FOUND);


  Function<String, Integer> parseInteger = (str) -> str == null ? null : Integer.parseInt(str);
  Function<String, Boolean> parseBoolean = (str) -> str == null ? null : Boolean.parseBoolean(str);
  Function<String, Timestamp> parseTimestamp = (str) -> str == null ? null : Timestamp.from(Instant.ofEpochSecond(Long.parseLong(str)));
  
  Function<User, User> fillUser = (e) -> {
    e.post = postService.getById(e.postId);
    e.user = userService.getById(e.userId);
    return e;
  };

  Function <Request, Request> fillRequest = (r) -> {
    r.creator = userService.getById(r.creatorId);
    r.user = userService.getById(r.userId);
    r.target = targetService.getById(r.targetId);
    r.target.post = postService.getById(r.target.postId);
    r.target.responsibleUser = userService.getById(r.target.userId);
    return r;
  };

  @RequestMapping(value="notification/new/")
  public ResponseEntity<?> newNotification(
      @RequestParam("userId")Integer senderId, 
      @RequestParam("text")String type)
  {
    if(postId != null && postId != null && type != null &&
        postService.exists(postId) && userService.exists(userId)) {
      User user = new User();
      user.postId = postId;
      user.userId = userId;
      user.time = new Timestamp(System.currentTimeMillis());
      user.type = Utils.valString(type);
      userService.add(user);
      return OK;
    } else {
      return BAD_REQUEST ;
    }
  }

  @RequestMapping(value="user/new/")
  public ResponseEntity<?> newUser(
      @RequestParam("userId")Integer userId, 
      @RequestParam("name")String name,
      @RequestParam("password")String password)
  {
    if(!userService.exists(userId)) {
      User u = new User();
      u.id = userId;
      u.name = Utils.valString(name);
      u.passwordHash = new BCryptPasswordEncoder().encode(password);
      u.permissionId = 0; //TODO auth
      userService.add(u);
      return OK;
    } else {
      return BAD_REQUEST;
    }
  }

  @RequestMapping(value="post/new/")
  public ResponseEntity<?> newPost(
      @RequestParam("name")String name, 
      @RequestParam("tags")String tags)
  {
    Post post = new Post();
    post.name = Utils.valString(name);
    post.tags = Utils.valString(tags);
    postService.add(post);
    return OK;
  }

  @RequestMapping(value="user/delete/")
  public ResponseEntity<?> deleteUser(
      @RequestParam(value="userId")Integer userId) {
    userService.delete(userId);
    return OK;
  }

  @RequestMapping(value="user/delete/")
  public ResponseEntity<?> deleteStudent(@RequestParam(value="userId")Integer userId)
  {
    userService.delete(userId);
    return OK;
  }

  @RequestMapping(value="post/delete/")
  public ResponseEntity<?> deletePost(@RequestParam(value="postId")Integer postId)
  {
    postService.delete(postId);
    return OK;
  }


  @RequestMapping(value="user/")
  public ResponseEntity<?> viewUser(@RequestParam Map<String,String> allRequestParam)
  {
    List<User> els = userService.query(
        parseInteger.apply(allRequestParam.get("count")),
        parseInteger.apply(allRequestParam.get("userId")), 
        parseInteger.apply(allRequestParam.get("userId")),
        parseInteger.apply(allRequestParam.get("postId")), 
        parseTimestamp.apply(allRequestParam.get("minDate")), 
        parseTimestamp.apply(allRequestParam.get("maxDate")),
        Utils.valString(allRequestParam.get("userName")),
        Utils.valString(allRequestParam.get("type")))
        .stream()
        .map(fillUser)
        .collect(Collectors.toList());
    return new ResponseEntity<>(els, HttpStatus.OK);
  }

  @RequestMapping(value="user/")
  public ResponseEntity<?> viewStudent(@RequestParam Map<String,String> allRequestParam)
  {
    if(allRequestParam.containsKey("userId")) {
      return new ResponseEntity<>(
          Arrays.asList(userService.getById(Integer.parseInt(allRequestParam.get("userId")))),
          HttpStatus.OK
          );
    } else if(allRequestParam.containsKey("name")){
      return new ResponseEntity<>(
          userService.getByName(allRequestParam.get("name")),
          HttpStatus.OK
          );
    } else {
      return new ResponseEntity<>(
          userService.getAll(),
          HttpStatus.OK
          );
    }
  }

  @RequestMapping(value="post/")
  public ResponseEntity<?> viewPost(@RequestParam Map<String,String> allRequestParam)
  {
    if(allRequestParam.containsKey("postId")) {
      return new ResponseEntity<>(
          Arrays.asList(postService.getById(Integer.parseInt(allRequestParam.get("postId")))),
          HttpStatus.OK
          );
    } else {
      return new ResponseEntity<>(
          postService.getAll(), 
          HttpStatus.OK
          );
    }
  }

}
