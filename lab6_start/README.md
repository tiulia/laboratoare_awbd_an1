
# Project 6

This project contains examples of Mockito tests, validations and error handling.
- add tests for Controllers.
- examples of using HandlerExceptionResolver.
- validate forms.

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

**@MockBean** adds mock objects to Spring application 
context. The mock will replace any existing bean of 
the same type in the application context.

#### Exercise 1

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

#### Exercise 2
Add the test _showById_.

```java
@Test
public void showById() {
    Long id = 1l;
    Product productTest = new Product();
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

#### Exercise 3
Add Mocks for productServie and for Model in the test class
ProductControllerTest

```java
@MockBean
ProductService productService;

@MockBean
Model model;
```

#### Exercise 4
Set the behaviour of th productService in _showByIdMvc()_ test.

```java
Long id = 1l;
ProductDTO productTestDTO = new ProductDTO();
productTestDTO.setId(id);
productTestDTO.setName("test");

when(productService.findById(id)).thenReturn(productTestDTO);

mockMvc.perform(get("/product/edit/{id}", "1"))
        .andExpect(status().isOk())
        .andExpect(view().name("productForm"))
        .andExpect(model().attribute("product", productTestDTO));
```

#### Exercise 5
Add test _showProductFormAdmin_.

```java
@Test
@WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
public void showProductFormAdmin() throws Exception {
    
    mockMvc.perform(get("/product/form"))
            .andExpect(status().isOk())
            .andExpect(view().name("productForm"))
            .andExpect(model().attributeExists("product"));
}
```


#### Exercise 6
Add test _getImage_.

```java
@Test
@WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
public void getImage() throws Exception {
    Long id = 1l;
    ProductDTO productTestDTO = new ProductDTO();
    productTestDTO.setId(id);
    productTestDTO.setName("test");

    Info info =  new Info();
    byte[] imageBytes = { 0x12, 0x34, 0x56, 0x78};
    info.setPhoto(imageBytes);
    productTestDTO.setInfo(info);

    when(productService.findById(id)).thenReturn(productTestDTO);
    
    mockMvc.perform(get("/product/getimage/{id}", "1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.IMAGE_JPEG));

}
```
    
#### Exercise 7
Add a test for POST product method.

```java
@Test
@WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
public void testSaveOrUpdate_WithValidProductAndNoFile_ShouldSaveProduct() throws Exception {
    ProductDTO product = new ProductDTO();
    product.setName("Test Product");

    mockMvc.perform(MockMvcRequestBuilders.multipart("/product").file("imagefile", new byte[0])
                    .with(csrf())
                    .param("name", "Test Product")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.TEXT_HTML))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/product"));

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

#### Exercise 7
Create a new package com.awbd.lab6.exceptions and 
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

#### Exercise 8
Throw a ResourceNotFoundException error when 
the product id is not found in the database, 
modify methods findById in ProductService.
Test http://localhost:8080/product/edit/10

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

#### Exercise 9
Annotate ResourceNotFoundException with @ResponseStatus.

```java
@ResponseStatus
public class ResourceNotFoundException extends RuntimeException {
}
```

#### Exercise 10
Add an @ExceptionHandler method for 
ResourceNotFoundException.class in ProductController.

```java
@ExceptionHandler(ResourceNotFoundException.class)
public ModelAndView handlerNotFoundException(Exception exception){
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.getModel().put("exception",exception);
    modelAndView.setViewName("notFoundException");
    return modelAndView;
}
```

#### Exercise 11
Add a test setting the productService to throw ResourceNotFoundException.

```java
@Test
@WithMockUser(username = "admin", password = "12345", roles = "ADMIN")
public void showByIdNotFound() throws Exception {
    Long id = 1l;

    when(productService.findById(id)).thenThrow(ResourceNotFoundException.class);

    mockMvc.perform(get("/product/edit/{id}", "1"))
            .andExpect(status().isNotFound())
            .andExpect(view().name("notFoundException"))
            .andExpect(model().attributeExists("exception"));
}
```

#### Exercise 12
Annotate handlerNotFoundException method with @ResponseStatus(HttpStatus.NOT_FOUND). 
Re-run tests.

```java
@ResponseStatus(HttpStatus.NOT_FOUND)
@ExceptionHandler(ResourceNotFoundException.class)
public ModelAndView handlerNotFoundException(Exception exception){
    ModelAndView modelAndView = new ModelAndView();
    modelAndView.getModel().put("exception",exception);
    modelAndView.setViewName("notFoundException");
    return modelAndView;
}
```

#### Exercise 13
Add a @ControllerAdvice class which will handle Exceptions globally, for all controllers.

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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

#### Exercise 14
Test http://localhost:8080/product/edit/abc
Create a SimpleMappingExceptionResolver bean 
that will map NumberFormatException to a default view, 
_defaultException.html_.


```java
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    @Bean(name = "simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver
    getSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver r =
                new SimpleMappingExceptionResolver();

        r.setDefaultErrorView("defaultException");
        r.setExceptionAttribute("ex");     // default "exception"

        return r;
    }
}
```

#### Exercise 15
Add mapping for specific Exceptions, setting the views and the Status Codes. 

```java
SimpleMappingExceptionResolver r =
        new SimpleMappingExceptionResolver();
//...
Properties mappings = new Properties();
mappings.setProperty("NumberFormatException", "numberFormatException");
r.setExceptionMappings(mappings);

Properties statusCodes = new Properties();
statusCodes.setProperty("NumberFormatException", "400");
r.setStatusCodes(statusCodes);
```

### Validatin API

Java bean validation API from Hibernate 
allows to express and validate application 
constraints ensuring that beans meet specific criteria.

Examples of annotations:
- **@Size**: specifies filed length. 
- **@Min @Max**: specifies min value and max value for the files.
- **@Pattern**:	check regular expressions. 
- **@NotNull**: check that the files is not null.

pom.xml dependencies:
```
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
</dependency>

<dependency>
	<groupId>org.hibernate.validator</groupId>
	<artifactId>hibernate-validator-annotation-processor</artifactId>
</dependency>
```

#### Exercise 16
Add annotations to ensure that the minimum price 
for a product is 100. 
Check that the product name contains only letters.

```java
@Setter
@Getter
@Entity
public class Product {

    //...
    
    @Pattern(regexp = "[A-Z]*", message = "only letters")
    private String name;

    @Min(value = 100, message = "min price 100")
    private Double reservePrice;
    
    //...
}
```

#### Exercise 17
Add annotations to ensure that the minimum price
for a product is 100.
Check that the product name contains only letters.

```java
@Setter
@Getter
@Entity
public class Product {

    //...
    
    @Pattern(regexp = "[A-Z]*", message = "only capital letters")
    private String code;

    @Min(value = 100, message = "min price 100")
    private Double reservePrice;
    
    //...
}
```

#### Exercise 18
Return the form if it has error. Change saveOrUpdate method in ProductController.

```java
@PostMapping("")
public String saveOrUpdate(@Valid @ModelAttribute ProductDTO product,
                            BindingResult bindingResult,
                            @RequestParam("imagefile") MultipartFile file
){
    if (bindingResult.hasErrors())
        return "productForm";
    //...
}
```

### Docs

[1] https://www.baeldung.com/mockito-argumentcaptor

[2] https://www.baeldung.com/mockito-argumentcaptor
 
[3] https://spring.io/guides/gs/testing-web/

[4] https://www.baeldung.com/integration-testing-in-spring

[5] https://www.baeldung.com/spring-boot-testing

[6] https://docs.spring.io/spring-boot/docs/1.5.3.RELEASE/reference/html/boot-features-testing.html

[7] https://www.baeldung.com/java-spring-mockito-mock-mockbean

[8] https://www.baeldung.com/spring-response-status

[9] https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc

[10] http://hibernate.org/validator/

[11] https://www.baeldung.com/javax-validation

[12] https://www.infoworld.com/article/3543268/junit-5-tutorial-part-2-unit-testing-spring-mvc-with-junit-5.html

[13]  https://www.baeldung.com/junit-5-extensions





