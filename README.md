# RestVertx
RestVertx is a mini-framework that makes it easier to build HTTP services with Vert.x

### Main Features

##### Feature #1: Easily create http endpoint handler methods in Java classes to be served by Vertx
Simply call RestVertx.register() in your constructor and annotate your methods

Example:

```java
@Base("items")
public class ShoppingList {

	public ShoppingList(Vertx _vertx, Router router)
	{				
		RestVertx.register(_vertx, router, this);
	}
}
```

##### Feature #2: Enable CORS
Great for cross-IDE development.  Are you transferring your web application code from one IDE to another because you prefer one for web development and the other for your Vertx endpoints?  Do you want to serve files with node in one IDE while editing your Vertx java files in Eclipse?

Simply call CORS.allowAll() in your App setup before the ending request handler, or else use the CORS annotation to open up a specific handling method

Example: <span style="color:red"> enable CORS for a specific method:</span>

```java

	@Method("Get")
	@CORS("http://localhost:3000")
	@Path("/")
	public String getItems()
	{
		...
	}
```
Example: <span style="color:red"> enable CORS at a higher level:</span>

```java

	@Override
	public void start() throws Exception {

		HttpServer server = vertx.createHttpServer();

		server = vertx.createHttpServer();

		router = Router.router(vertx);

		// Enable CORS
		// This is useful for developing on a node-dependent IDE where node serves the app, but your service endpoints exist in Vertx
	  CORS.allow(router, "http://localhost:3000");
		...
	}
```

##### Feature #3: Autobind JSON arguments to model parameters
Let's say you have a several variables, and/or nested objects you need to pass in to your endpoint as arguments.  You can create a model which contains your variables and/or nested objects and use it as the parameter in your handling method.  If you send a JSON object in your request, it will automatically be deserialized into the model (using FasterJackson databind, core, and annotations - https://github.com/FasterXML/jackson-core).

Simply specify the model as the parameter in both the endpoint and handling method and send a valid JSON object in the request

Example:

```java
// Vertx handling method in Java
	@Method("Post")
	@ResultType("json")
	@Path("shoppingLists/:request")
	public String getShoppingListPost(ShoppingListRequest request)
	{		
		String test = manager.getShoppingList(request.getId());
		return test;
	}
```
```java
// java ShoppingListRequest model w/Jackson annotations
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_EMPTY)
	public class ShoppingListRequest {

		private String id;
		private String name;
		private GroceryStore store;

		public ShoppingListRequest() {
			...
		}

		// Getters/Setters
		...
	}
```
``` javascript
// javascript request object before stringifying
var request = {
	id: "1",
	name: "Angela",
	store: {
		name: "fake store"
	}
}
```

# Instructions

## Prerequisites

- Vert.x version 3+
- Maven
- Your favorite java editor
- A cup of coffee :)

## Add to your project

#### In your Maven Project's POM file, do the following:

Add this to your repositories:

```xml
<repository>
    <id>git-codesipcoffee</id>
    <name>CodeSipCoffee's Git based repo</name>
    <url>https://raw.github.com/codesipcoffee/RestVertx/releases</url>
</repository>
```

Add this in your dependencies:

```xml
<dependency>
	<groupId>code.sip.coffee</groupId>
	<artifactId>restvertx</artifactId>
	<version>0.0.4</version>
</dependency>
```

## How to use RestVertx

1.Use static method RestVertx.register() in the handling class (no need to extend RestVertx)

``` java
public ShoppingListFinder(Vertx _vertx, Router router)
{
	RestVertx.register(_vertx, router, this);
}
```

2.Add your annotations to your handling class(es)

```java
// Vertx handling method in Java
	@Method("Post")
	@ResultType("json")
	@Path("shoppingLists/:request")
	public String getShoppingListPost(ShoppingListRequest request)
	{		
		String test = manager.getShoppingList(request.getId());
		return test;
	}
```

3.Instantiate new instance of handling class in your app

``` java
@Override
public void start() throws Exception {

		  HttpServer server = vertx.createHttpServer();

		  server = vertx.createHttpServer();

		  router = Router.router(vertx);

		  // Register your custom handlers
		  RegisterRoutes();
			...}
			private void RegisterRoutes()
			{
		  	ShoppingListFinder mng = new ShoppingListFinder(vertx, router);
			}
```

4.Make sure you set compiler to remember parameter values for your project

That's it, you're done

## Annotations

@Path
Required
example: @Path("name/:id")

- If you don't set the path, then the method will skipped
- At a minimum, make sure to set the Path as "/"
- In the example path, :id is a path variable
- If you set your project to remember parameters in your build arguments, then path variable will be matched to parameters, no matter the order in the path

- If you don't set your project to remember params in your build arguments, then path variables will be processed as if they are in the same order as the parameters (even if they're not).  <span style="color:blue">It is highly recommended to set your compiler to remember params for your project to get the most benefit out of RestVertx</span>

@Method
Optional
example: @Method("Get")

- If you don't set the method, we will attempt to determine if the name of the method is an http method and set the method to that name
- Defaults to Get

@Base (Class Annotation)
Optional
example: @Base("api/monkeys")

- Set base path for all methods in the class

@ResultType
Optional
example: @ResultType("json")

- Sets the return type, which affects the header info as well

@RestIgnore
Optional
example: @RestIgnore

- Ignores the method
- Note that any method not specifying a path is also ignored

@CORS
Optional
example: @CORS and @CORS("http://localhost:3000")

- Enables CORS on a specific method(s) instead of across the board

## Important Disclaimers

As of 9/9 v0.0.4, this is not production ready and likely has bugs lurking underneath... ;)

Any comments/questions, see the <a href='https://groups.google.com/forum/#!forum/restvertx' target="_blank">RestVertx Google Group</a>

Please submit any issues you find

RestVertx is written in Java
