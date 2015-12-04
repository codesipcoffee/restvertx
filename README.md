# RestVertx
RestVertx is a mini-framework that makes it easier to build HTTP services with Vert.x

**** <b>Latest Version released 12/3/2015</b> ****

[Main Features](#Main-Features)<br/>
[Getting Started](#Getting-Started)<br/>
[Annotations](#Annotations)<br/>
[Benchmarks](#Benchmarks)<br/>
[Tests](#Tests)<br/>
[Example on GitHub](https://github.com/codesipcoffee/restvertx/tree/example)

<a name="Main-Features"/>
### Main Features

##### Feature #1: Easily create http endpoint handler methods in Java classes to be served by Vertx
Simply call RestVertx.register() in your constructor and annotate your methods.  Instantiate the handling class(es) in your verticle and you're ready to go!

Example constructor of handling class:

```java
@Base("items")
public class ShoppingList {

	public ShoppingList(Vertx _vertx, Router router)
	{				
		RestVertx.register(_vertx, router, this);
	}
}
```

(Read Feature #2 below to see examples of handling methods)

##### Feature #2: Autobind JSON arguments to model parameters
Let's say you have a several variables, and/or nested objects you need to pass in to your endpoint as arguments.  You can create a model which contains your variables and/or nested objects and use it as the parameter in your handling method.  If you send a JSON object in your request (via path param or request body), it will automatically be deserialized into the model (using FasterJackson databind, core, and annotations - https://github.com/FasterXML/jackson-core).

Simply specify the model as the parameter in both the endpoint and handling method and send a valid JSON object in the request

Example handling method using URL encoded Json in the path param for Json object:

```java
	@Method("Post")
	@ResultType("json")
	@Path("shoppingLists/:request")
	public String getShoppingListPost(ShoppingListRequest request)
	{		
		String test = manager.getShoppingList(request.getId());
		return test;
	}
```

Example handling method using Json in the request body (automatically detected and deserialized):

```java
	@Method("Post")
	@ResultType("json")
	@Path("shoppingLists")
	public String getShoppingListPost(ShoppingListRequest request)
	{		
		String test = manager.getShoppingList(request.getId());
		return test;
	}
```

Example data model that RestVertx deserializes into w/Jackson annotations (handling method argument):

```java
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

Example on the client side a request to be sent before stringifying:

``` javascript
var request = {
	id: "1",
	name: "Angela",
	store: {
		name: "fake store"
	}
}
```

More examples can be found in the testing source files of the main branch (newer example) as well as in the example branch (older example)

##### Feature #3: Enable CORS
Great for cross-IDE development.  Are you transferring your web application code from one IDE to another because you prefer one for web development and the other for your Vertx endpoints?  Do you want to serve files with node in one IDE while running your Vertx service endpoints in Eclipse on a different port?

Simply call CORS.allowAll() in your App setup before the request handlers in your verticle, or else use the CORS annotation to open up a specific handling method

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

<a name=Getting-Started />
# Getting Started

## Prerequisites

- Vert.x version 3+
- Maven
- Your favorite java editor
- A cup of coffee or espresso drink :)

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
	<version>0.0.5</version>
</dependency>
```

(You may need to do a maven force update, please let me know if you experience issues downloading latest artifact with the info above)

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

3.Instantiate new instance of handling class in your app (don't forget to include the body handler!)

``` java
@Override
public void start() throws Exception {

		  HttpServer server = vertx.createHttpServer();

		  server = vertx.createHttpServer();

		  router = Router.router(vertx);

			// So we can use getBodyAsJson() and/or getBodyAsString() in our handling methods
		  router.route().handler(BodyHandler.create());

		  // Register your custom handlers
		  RegisterRoutes();
			...}
			private void RegisterRoutes()
			{
		  	ShoppingListFinder mng = new ShoppingListFinder(vertx, router);
			}
```

4.Make sure you set compiler to remember parameter values for your project

That's it, you're done.  Just enough time left to make another espresso ;)

<a name=Annotations />
## Annotations

<span style="color:rgb(21, 186, 1)">@Path</span><br/>
Required<br/>
example: @Path("name/:id")

- If you don't set the path, then the method will skipped
- At a minimum, make sure to set the Path as "/"
- In the example path, :id is a path variable
- If you set your project to remember parameters in your build arguments, then path variable will be matched to parameters, no matter the order in the path

- If you don't set your project to remember params in your build arguments, then path variables will be processed as if they are in the same order as the parameters (even if they're not).  <span style="color:rgb(54, 108, 212)">It is highly recommended to set your compiler to remember params for your project to get the most benefit out of RestVertx</span>

<span style="color:rgb(21, 186, 1)">@Method</span><br/>
Optional<br/>
example: @Method("Get")

- If you don't set the method, we will attempt to determine if the name of the handling method is or contains an http method name and set the http method registered for the route to that name
- Defaults to Get

<span style="color:rgb(21, 186, 1)">@Base </span> (<b>Class Annotation</b>)<br/>
Optional<br/>
example: @Base("api/monkeys")

- Set base path for all methods in the class

<span style="color:rgb(21, 186, 1)">@ResultType</span><br/>
Optional<br/>
example: @ResultType("json")

- Sets the return type, which affects the header info as well
- <b>Don't specify a result type unless it's JSON or a file</b>

<span style="color:rgb(21, 186, 1)">@RestIgnore</span><br/>
Optional<br/>
example: @RestIgnore

- Ignores the method
- Note that any method not specifying a path is also ignored

<span style="color:rgb(21, 186, 1)">@CORS</span><br/>
Optional<br/>
example: @CORS and @CORS("http://localhost:3000")

- Enables CORS on a specific method(s) instead of across the board
- Can optionally specify the ip/port

<a name=Benchmarks />
## Benchmarks
The times taken to make 50,000 synchronous, consecutive POST request where the handling method deserialized JSON argument to variable and serialized variable back to JSON argument before returning/ending

Times should only be compared relative to one another to give a very rough estimate of time differences between using RestVertx on top of Vert.x vs Vert.x alone.  There is always a cost for adding a layer on top of something else

While one test ran, the other @test annotation AND the other route were commented out in TimeTest.java (included under testing source code)

RestVertx + Vert.x

	Time taken = 39781 ms
	Time taken (nano) = 39781703710

	Time taken = 39835 ms
	Time taken (nano) = 39835314664

Vert.x alone

	Time taken Vert.x alone = 35949 ms
	Time taken (nano) Vert.x alone = 35949119862

	Time taken Vert.x alone = 35721 ms
	Time taken (nano) Vert.x alone = 35721488940


<a name=Tests />
## Tests
we are trying to include more tests with our releases.  All tests written are passing as of 12/3

## Important Disclaimers

As of 12/3 v0.0.5, this is not production ready and may have bugs lurking underneath.

Testing & documentation is more important than benchmarks for this project, please include tests with any pull requests you make [here](https://github.com/codesipcoffee/restvertx/issues)

Any comments/questions, see the <a href='https://groups.google.com/forum/#!forum/restvertx' target="_blank">RestVertx Google Group</a>

Please submit any issues you find

RestVertx is written in Java
