
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


### Docs

[1] https://docs.spring.io/spring-framework/reference/web/webmvc.html

[2] https://www.baeldung.com/spring-template-engines

[3] https://www.baeldung.com/maven-webjars

[4] https://www.baeldung.com/spring-mvc-model-model-map-model-view

[5] https://www.baeldung.com/entity-to-and-from-dto-for-a-java-spring-application

[6] https://www.baeldung.com/java-performance-mapping-frameworks
