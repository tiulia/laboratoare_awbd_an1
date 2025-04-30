
# Project 4

This project contains examples of Tymeleaf views. In the next steps we will:
- add controllers.
- interact with Tymeleaf views.
- add validations and error handling.
- add examples tests for Controllers using Mockito.

![External Image](https://bafybeigkeme7uhrm2fenjx5kyrfcqz3525g3dpxrnn2piuq2bjku2zk4fu.ipfs.w3s.link/erd_jpa.png)

### Project configuration

#### Spring MVC

Spring MVC framework is designed around a central Servlet: **DispatcherServlet** that dispatches requests to controllers.
**WebApplicationContext** contains:
- **HandlerMapping**: maps incoming requests to handlers. The most common implementation is 	based on annotated Controllers 
- **HandlerExceptionResolver**: maps exceptions to views.
- **ViewResolver**: resolves string-based view names based on view types.  

![External Image](https://bafybeiajdnogimsw23jrtdpjlfv4ftnf3tvqemfzjrs3lzah2nwumbyjma.ipfs.w3s.link/mvc.png)


Spring MVC supports a variety of view technologies, such as:
- JSP (JavaServer Pages): A classic technology for creating dynamic web pages with embedded Java code.
- Thymeleaf: A modern server-side Java template engine that emphasizes natural HTML.
- FreeMarker: Another powerful and flexible template engine.
- Velocity: A simple and elegant template engine.


The configuration of a ViewResolver specifies: 

- which view technology to use.
- how to locate the view files.


#### Tymeleaf

We will use maven dependency for Tymeleaf.
Spring Boot will autoconfigure a ViewResolver based on configuration
we provide for specific technologies, i.e. for a specific Template Engine.
A ViewResolver provides a mapping between view names and actual views.

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

#### WebJars
WebJars simplify the process of managing and including client-side web libraries like JavaScript and CSS  in Java. 
WebJars allows the declaration of libraries as dependencies 
in build configuration files (such as Maven or Gradle). 
When the project is build, web-jar dependencies are fetched automatically from a Maven repository and included in the project's classpath.

```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>webjars-locator</artifactId>
  <version>0.5</version>
</dependency>
```
```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>bootstrap</artifactId>
  <version>5.3.3</version>
</dependency>
```

```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>jquery</artifactId>
  <version>3.7.1</version>
</dependency>
```
```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>font-awesome</artifactId>
    <version>6.5.2</version>
</dependency>
```

### Controllers and Views

- **Model**  acts as a container for application data 
intended for display in the user interface (the view). It essentially holds the attributes necessary for rendering dynamic web pages.

Methods within Spring MVC 
controllers, specifically those
annotated with @RequestMapping, can 
accept an argument of type Model. 
This provides a convenient way 
to pass data from the controller 
to the view.

Data is added to the Model using a 
map-like structure via the 
_addAttribute_(String name, Object value) 
method. 
Here, name serves as the key 
under which the value (the data object) 
will be accessible within the view 
(e.g., in JSP or Thymeleaf template). 
Spring MVC then makes this Model object 
available to the configured 
view resolver and ultimately 
to the chosen view technology 
for rendering the response to the client.

- **ModelAndView** stores both the model and the view template that will be used 
by the TemplateEngine to render the response delivered to the client. 
Model attributes are store as a map and added with addObject.


#### Exercise 1
Add a new controller, ProductController. This controller will serve views interacting 
with resources of type Product.


```java 	
@Controller
@RequestMapping("/products")
public class ProductController {
  ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @RequestMapping("")
  public String productList() {
    return "productList";
  }

}
```


### Tymeleaf Iterations
Thymeleaf is a Java template engine for processing HTML, XML, CSS etc. 

Model attributes from Spring are available in Thymeleaf as “context variables”. 
Context variables are accessed with Spring EL expressions. 
Spring Expression Language is a language for query and manipulate object graph at runtime. 

Model Attributes are accessed with: 
${attributeName}

Request parameters are accessed with the syntax:
```
${param.param_name}
```

**th:each** iterates collections (java.util.Map, java.util.Arrays, java.util.Iterable etc.) 

```
<tr th:each="product : ${products}"> 
```

The following properties may be accessed via status variable: 
- index (iteration index, starting from 0)
- count (total number of elements processed) 
- size (total number of elements) 
- even/odd boolean 
- first (boolean – true if current element is the first element of the collection), last (boolean true if current element is the last element of the collection)  

```
<tr th:each="product, stat : ${products}"
    th:class="${stat.odd}? 'table-light':'table-dark'">
```


#### Exercise 2
Add Tymeleaf tags in productList.html view.


```html
<tr th:each="product, stat : ${products}"
    th:class="${stat.odd}? 'table-light':''">
  <td th:text="${product.id}">1</td>
  <td th:text="${product.name}">Product 1</td>
  <td th:text="${product.code}">Code</td>
  <td th:text="${product.reservePrice}">Reserved price</td>
  <td th:text="${product.reservePrice}">Best offer</td>
  <td><a href="#" th:href="@{'/products/edit/' + ${product.id}}"><i class="fa-solid fa-pen"></i></a></td>
  <td><a href="#" th:href="@{'/products/delete/' + ${product.id}}"><i class="fa-solid fa-trash"></i></a></td>
</tr>
```
#### Exercise 3
Update the ProductController to depend on the ProductService for retrieving product data. Pass the retrieved list of products to the view by adding it to the Model argument of the @RequestMapping handler method. 

```java 
@Controller
@RequestMapping("/products")
public class ProductController {
    ProductService productService;
    CategoryService categoryService;
    
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @RequestMapping("")
    public String productList(Model model) {
        List<ProductDTO> products = productService.findAll();
        model.addAttribute("products", products);
        return "productList";
    }
}
```

#### Exercise 4
Create a similar template for categories.

#### Exercise 5

Implement edit and delete 
functionality in the 
ProductController 
with corresponding 
endpoints 
at /edit/{productId} 
and /delete/{productId}. 
Additionally, fetch all 
categories and 
inject this list 
into the Model 
specifically 
for rendering the 
product details view.

```java 
@RequestMapping("/edit/{id}")
public String edit(@PathVariable String id, Model model) {
    model.addAttribute("product", productService.findById(Long.valueOf(id)));
    
    List<CategoryDTO> categoriesAll = categoryService.findAll();
    model.addAttribute("categoriesAll", categoriesAll );

        return "productForm";
}
```

```java 
@RequestMapping("/delete/{id}")
public String deleteById(@PathVariable String id){
  productService.deleteById(Long.valueOf(id));
  return "redirect:/products";
}
```

#### Exercise 6
Add method to handle file transfer to correctly update the image 
associated with the product.
In ProductService add:

```java 
@Override
public void savePhotoFile(ProductDTO productDTO, MultipartFile file) {
    Product product = modelMapper.map(productDTO, Product.class);
    try {

        byte[] byteObjects = new byte[file.getBytes().length];
        int i = 0; for (byte b : file.getBytes()){
            byteObjects[i++] = b; }

        Info info = product.getInfo();
        if (info == null) {
            info = new Info();
        }
        
        info.setProduct(product);
        product.setInfo(info);

        if (byteObjects.length > 0){
            info.setPhoto(byteObjects);
        }

        productRepository.save(product);
    }
    catch (IOException e) {
    }
}
```

In ProductController add:

```java 
@PostMapping("")
public String saveOrUpdate(@ModelAttribute ProductDTO product,
                               @RequestParam("imagefile") MultipartFile file){
    if (file.isEmpty())
        productService.save(product);
    else
        productService.savePhotoFile(product, file);


    return "redirect:/products" ;
}

```

```java 
@GetMapping("/getimage/{id}")
public void downloadImage(@PathVariable String id, HttpServletResponse response) throws IOException {
        ProductDTO productDTO = productService.findById(Long.valueOf(id));

        if (productDTO.getInfo() != null) {


            if (productDTO.getInfo().getPhoto() != null) {
                byte[] byteArray = new byte[productDTO.getInfo().getPhoto().length];
                int i = 0;
                for (Byte wrappedByte : productDTO.getInfo().getPhoto()) {
                    byteArray[i++] = wrappedByte;
                }
                response.setContentType("image/jpeg");
                InputStream is = new ByteArrayInputStream(byteArray);
                try {
                    IOUtils.copy(is, response.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
}
```

#### Exercise 7
Add the controller for Categories:
```java 
@Controller
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RequestMapping("")
    public String categoryList(Model model) {
        List<CategoryDTO> categories = categoryService.findAll();
        model.addAttribute("categories",categories);
        return "categoryList";
    }

}
```

### Tests
#### Argument captor

ArgumentCaptor is used to capture an argument which is passed
in the invocation of a method.
The constructor takes as argument
the type of the argument to be "captured".

Instead of using the ArgumentCaptor(type) constructor,
we can inject an ArgumentCaptor object with annotation @Captor

After invoking the method, captor.getValue() returns the value of the argument.

#### MockMVC
MockMvc object encapsulates web application beans
and allows testing web requests.
Available options are:

- Specifying headers for the request.
- Specifying request body.
- Validate the response:
    - check HTTP - status code,
    - check response headers,
    - check response body.

When running an integration test different layers of
applications are involved. @AutoConfigureMockMvc
annotation instructs Spring to create a MockMvc object,
associated with the application context,
prepared to send requests to TestDispatcherServlet
which is an extension of DispatcherServlet.  
Requests are sent by calling the perform method.
If @AutoConfigureMockMvc annotation is used,
MockMvc object can be injected with @Autowired annotation.

**@SpringBootTest** bootstraps the entire
Spring container.
Values for webEnvironment property of @SpringBootTest
annotation are:
- **RANDOM_PORT**: EmbeddedWebApplicationContext, real servlet environment.
  Embedded servlet containers are started and listening on a random port.

- **DEFINED_PORT**: EmbeddedWebApplicationContext, real servlet environment. 			Embedded servlet containers are started and listening on a defined port (i.e from  			application.properties or on the default port 8080).

- **NONE**: loads ApplicationContext using SpringApplication,
  does not provide any servlet environment.

#### Extensions
**Junit 5 extensions** extends the behavior
of test class or methods.
Extensions are related to a certain event
in the execution of a test (extension point).
For each extension point we implement an interface.
@ExtendWith annotation registers test extensions.

**MockitoExtension.class** finds member
variables annotated with **@Mock** and creates
a mock implementation of those variables.
Mocks are injected into member variables
annotated with the **@InjectMocks** annotation,
using either construction injection or setter injection.

**@MockitoBean** adds mock objects to Spring application
context. The mock will replace any existing bean of
the same type in the application context.

#### Exercise 8

Add a new test class _ProductServiceControllerTest_.

```java
@ExtendWith(MockitoExtension.class)
public class ProductServiceControllerTest {

  @Mock
  Model model;
  
  @Mock
  ProductService productService;
  
  @InjectMocks
  ProductController productController;

  @Mock
  CategoryService categoryService;
}
```

#### Exercise 9
Add a new test _showById_.

```java
@Test
public void showById() {
  Long id = 1L;
  Product productTest;

  productTest = new Product();
  productTest.setId(id);

  ProductDTO productTestDTO = new ProductDTO();
  productTestDTO.setId(id);

  when(productService.findById(id)).thenReturn(productTestDTO);

  String viewName = productController.edit(id.toString(), model);
  assertEquals("productForm", viewName);
  verify(productService, times(1)).findById(id);

  ArgumentCaptor<ProductDTO> argumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);
  verify(model, times(1))
          .addAttribute(eq("product"), argumentCaptor.capture() );

  ProductDTO productArg = argumentCaptor.getValue();
  assertEquals(productArg.getId(), productTestDTO.getId() );

}
```

#### Exercise 10
Add Mocks for productService and 
for Model in a test class
ProductServiceControllerTest

```java
@SpringBootTest
@AutoConfigureMockMvc
@Profile("mysql")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    ProductService productService;

    @MockitoBean
    CategoryService categoryService;

    @MockitoBean
    Model model;
}
```

#### Exercise 11
Set the behaviour of th productService in _showByIdMvc()_ test.

```java
    @Test
public void showByIdMvc() throws Exception {
  Long id = 1l;

  ProductDTO productTestDTO = new ProductDTO();
  productTestDTO.setId(id);
  productTestDTO.setName("test");

  when(productService.findById(id)).thenReturn(productTestDTO);

  mockMvc.perform(get("/products/edit/{id}", "1"))
          .andExpect(status().isOk())
          .andExpect(view().name("productForm"))
          .andExpect(model().attribute("product", productTestDTO));

}
```

#### Exercise 12
Add a test for POST request.

```java
    @Test
public void testSaveOrUpdate_WithValidProductAndNoFile_ShouldSaveProduct() throws Exception {
    ProductDTO product = new ProductDTO();
    product.setName("Test Product");

    mockMvc.perform(MockMvcRequestBuilders.multipart("/products").file("imagefile", new byte[0])
                  .param("name", "Test Product")
                  .contentType(MediaType.MULTIPART_FORM_DATA)
                  .accept(MediaType.TEXT_HTML))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl("/products"));

    ArgumentCaptor<ProductDTO> argumentCaptor = ArgumentCaptor.forClass(ProductDTO.class);
    verify(productService, times(1))
          .save(argumentCaptor.capture() );

    ProductDTO productArg = argumentCaptor.getValue();
    assertEquals(productArg.getName(), product.getName() );
}
```

### Exception handling
#### HandlerExceptionResolver

HandlerExceptionResolver is used internally by Spring
to intercept and process any exception raised in the MVC
system and not handled by a Controller. The parameter handler
refers to the controller that generated the exception.

```java
public interface HandlerExceptionResolver {
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex);
}
```

Three default implementations are created for
HandlerExceptionResolver and processed in order
by HandlerExceptionResolverComposite bean.

![External Image](https://bafybeibmb5nwkiojpumpdtgmpkz6pkttde4jzbxqtx5qdvjqp6knpvijhm.ipfs.w3s.link/execptions.jpg)

##### @ResponseStatus

@ResponseStatus annotate custom exception class
to indicate the HTTP status to be return
when the exception is thrown.
Examples of status codes:
- Client Errors: 400 Bad Request.
- 401 Unauthorized: Authentication Required.
- 404 Not Found: Resource not found.
- 405 Method not Allowed.
- 403 Forbidden: Unauthorized.
- 500 Server error: Server Unhandled exceptions.

##### @ExceptionHandler

@ExceptionHandler defines custom exception handling
at Controller level:
- define a specific status code.
- return a specific view with details about the error.
- interact with ModelAndView objects.
- within an @ExceptionHandler method, we don't have direct
  access to the Model object. We can't directly add attributes
  to the model.

##### SimpleMappingExceptionResolver
- Maps exception class names to view names.
- Specifies a fallback error page for exceptions not associated with a specific view.
- Adds exception attribute to the model.

#### Exercise 13
Create a new package com.awbd.lab4.exceptions and
a custom exception class that will be thrown if a product
id is not found in the database.

```java
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException() {
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
```

#### Exercise 14
Throw a ResourceNotFoundException error when
the product id is not found in the database,
modify methods findById in ProductService.
Test http://localhost:8080/products/edit/10

```java
@Override
public ProductDTO findById(Long l) {
    Optional<Product> productOptional = productRepository.findById(l);
    if (!productOptional.isPresent()) {
        throw new ResourceNotFoundException("product " + l + " not found");
        //throw new RuntimeException("Product not found!");
    }
    return modelMapper.map(productOptional.get(), ProductDTO.class);
}
```

#### Exercise 15
Annotate ResourceNotFoundException with @ResponseStatus.

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
}
```

#### Exercise 16
Add a GlobalExceptionHandler

```java
@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(ResourceNotFoundException.class)
  public ModelAndView handlerNotFoundException(Exception exception){
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.getModel().put("exception",exception);
    modelAndView.setViewName("notFoundException");
    return modelAndView;
  }

}
```


### Docs

[1] https://docs.spring.io/spring-framework/reference/web/webmvc.html

[2] https://www.baeldung.com/spring-template-engines

[3] https://www.baeldung.com/maven-webjars

[4] https://www.baeldung.com/spring-mvc-model-model-map-model-view

[5] https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application

[6] https://www.baeldung.com/java-performance-mapping-frameworks
