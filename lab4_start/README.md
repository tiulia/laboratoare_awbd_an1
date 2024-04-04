
# Project 4

This project contains examples of Tymeleaf views.
- adding mappers.
- adding services. 
- adding controllers.
- interact with Tymeleaf views.

![External Image](https://bafybeigkeme7uhrm2fenjx5kyrfcqz3525g3dpxrnn2piuq2bjku2zk4fu.ipfs.w3s.link/erd_jpa.png)

### Project configuration

#### Spring MVC

Spring MVC framework is designed around a central Servlet: **DispatcherServlet** that dispatches requests to controllers.
**WebApplicationContext** contains:
- **HandlerMapping**: maps incoming requests to handlers. The most common implementation is 	based on annotated Controllers 
- **HandlerExceptionResolver**: maps exceptions to views.
- **ViewResolver**: resolves string-based view names based on view types.  

![External Image](https://bafybeiajdnogimsw23jrtdpjlfv4ftnf3tvqemfzjrs3lzah2nwumbyjma.ipfs.w3s.link/mvc.png)

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
  <version>0.50</version>
</dependency>
```
```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>bootstrap</artifactId>
  <version>5.3.2</version>
</dependency>
```

```xml
<dependency>
  <groupId>org.webjars</groupId>
  <artifactId>jquery</artifactId>
  <version>3.6.0</version>
</dependency>
```
```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>font-awesome</artifactId>
    <version>6.4.0</version>
</dependency>
```

### Controllers and View

- **Model** encapsulates the data that is needed
to be displayed in the user interface.
Model holds the attributes for rendering views.
Methods annotated with @RequestMapping accept an attribute  of type Model. 
Data is added in a map-type date structure using _addAttribute_ method.


- **ModelAndView** stores both the model and the view template that will be used 
by the TemplateEngine to render the response delivered to the client. 
Model attributes are store as a map and added with addObject.


#### Example 1
Request _getHome_ from class MainControllerAdd 
will serve the main page. The argument of ModelAndView is the 
name of the view to be found in resources/templates.

```java
@Controller
public class MainController {
    
  @RequestMapping({"","/","/home"})
  public ModelAndView getHome(){

    return new ModelAndView("main");
  }

}
```

### Mappers
#### DTOs - Data Transfer Objects
Data Transfer Objects are POJOs used to encapsulate 
and transfer data between different layers of an application: 
- between the presentation layer (UI) and the business logic layer. 
- between the business logic layer and the data access layer.

Advantages of using DTOs are:
- serialization: easily converted into json, xml or other formats.
- reduce unnecessary data-transfer: include only relevant information.
- reduce dependencies, different parts of the application are decoupled.

We can define mappers, or we can use libraries such as _MapStruct_ or _ModelMapper_.

**ModelMapper** automatically maps fields with the same names and data types.
ModelMapper supports complex mapping scenarios without the need for explicit mapping interfaces.

**MapStruct** requires explicit mapping interfaces to be defined by the developers.


ModelMapper relies on reflection while MapStruct uses compile-time code generation.
Hence, MapStruct has a better performance, being slight faster.

#### Exercise 2
Add packages dtas and mappers.
Add DTOs and mappers for Category.

```java
@Setter
@Getter
@AllArgsConstructor
public class CategoryDTO {

    private Long id;
    private String name;

}
```

```java
@Component
public class CategoryMapper {
  public CategoryDTO toDto(Category category) {
    Long id = category.getId();
    String name= category.getName();
    return new CategoryDTO(id, name);
  }

  public Category toCategory(CategoryDTO categoryDTO) {
    Category category = new Category();
    category.setId(categoryDTO.getId());
    category.setName(categoryDTO.getName());
    return category;
  }
}
```


#### Exercise 3
Check the depenndcy for modelmapper and a bean of type ModelMapper in Lab4Application 

```xml
<dependency>
  <groupId>org.modelmapper</groupId>
  <artifactId>modelmapper</artifactId>
  <version>3.2.0</version>
</dependency>
```

```java
@Bean
public ModelMapper modelMapper() {
    return new ModelMapper();
}
```

#### Exercise 4
Add DTO class for Product. We will map Product to ProductDTO with ModelMapper, hence we
don't need to write a specific mapper interface.

```java
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {

  private Long id;
  private String name;
  private String code;
  private Double reservePrice;
  private Boolean restored;
  private Info info;
  private Participant seller;
  private List<Category> categories;
  private Currency currency;

}
```

#### Exercise 5
Check maven configuration for MapStruct. 
Annotation processors are tools that process annotations in Java source code.
We must MapStruct to generate mapprs during the compilation process based on the annotations. 

```
<dependency>
  <groupId>org.mapstruct</groupId>
  <artifactId>mapstruct</artifactId>
  <version>1.6.0.Beta1</version>
</dependency>
```

```
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.11.0</version>
  <configuration>
	<annotationProcessorPaths>
			<path>
			<groupId>org.mapstruct</groupId>
			<artifactId>mapstruct-processor</artifactId>
		  <version>1.6.0.Beta1</version>
		</path>
		<path>
		  <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
      </path>
    </annotationProcessorPaths>
  </configuration>
</plugin>
```

#### Exercise 6
Add DTO and mapper for Participant.

```java
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDTO {

    private Long id;
    private String lastName;
    private String firstName;
    private java.util.Date birthDate;

    private List<Product> products;

}
```
A bean implementing ParticipantMapper will be automatically created.

```java
@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    ParticipantDTO toDto (Participant participant);
    Participant toParticipant (ParticipantDTO participantDTO);
}
```

### Stereotype annotations

Stereotype annotations are used for classifications:

- **@Service**: Represents a component holding the business logic, typically used in the service layer.


- **@Repository**: Represents a component in the persistence layer, used for database repository operations.

#### Exercise 7
Add a new package com.awbd.lab4.services and a new interface com.awbd.lab4.services._ProductsService_:
Implement com.awbd.lab4.services.ProductsService using a bean of type _ProductRepository_.

```java
public interface ProductService {
  List<ProductDTO> findAll();
  ProductDTO findById(Long l);
  ProductDTO save(ProductDTO product);
  void deleteById(Long id);

}
```

```java
@Service
public class ProductServiceImpl implements ProductService {
  ProductRepository productRepository;

  ModelMapper modelMapper;

  public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
    this.productRepository = productRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public List<ProductDTO> findAll(){
    List<Product> products = new LinkedList<>();
    productRepository.findAll(Sort.by("name")
    ).iterator().forEachRemaining(products::add);

    return products.stream()
            .map(product -> modelMapper.map(product, ProductDTO.class))
            .collect(Collectors.toList());
  }

  @Override
  public ProductDTO findById(Long l) {
    Optional<Product> productOptional = productRepository.findById(l);
    if (!productOptional.isPresent()) {
      throw new RuntimeException("Product not found!");
    }
    return modelMapper.map(productOptional.get(), ProductDTO.class);
  }

  @Override
  public ProductDTO save(ProductDTO product) {
    ;
    Product savedProduct = productRepository.save(modelMapper.map(product, Product.class));
    return modelMapper.map(savedProduct, ProductDTO.class);
  }

  @Override
  public void deleteById(Long id) {
    productRepository.deleteById(id);
  }
}
```

#### Exercise 8
Add the interface and the implementation for CategoryService.

```java
@Service
public interface CategoryService {
    List<CategoryDTO> findAll();
    CategoryDTO findById(Long l);
    CategoryDTO save(CategoryDTO category);
    void deleteById(Long id);

}
```

```java
@Service
public class CategoryServiceImpl implements CategoryService{

  private CategoryRepository categoryRepository;
  private CategoryMapper categoryMapper;

  CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper){
    this.categoryRepository = categoryRepository;
    this.categoryMapper = categoryMapper;
  }

  @Override
  public List<CategoryDTO> findAll(){
    List<Category> categories = new LinkedList<>();
    categoryRepository.findAll().iterator().forEachRemaining(categories::add);

    return categories.stream()
            .map(categoryMapper::toDto)
            .collect(Collectors.toList());
  }

  @Override
  public CategoryDTO findById(Long l) {
    Optional<Category> categoryOptional = categoryRepository.findById(l);
    if (!categoryOptional.isPresent()) {
      throw new RuntimeException("Category not found!");
    }

    return categoryMapper.toDto(categoryOptional.get());
  }

  @Override
  public CategoryDTO save(CategoryDTO categoryDto) {
    Category savedCategory = categoryRepository.save(categoryMapper.toCategory(categoryDto));
    return categoryMapper.toDto(savedCategory);
  }

  @Override
  public void deleteById(Long id) {
    categoryRepository.deleteById(id);
  }


}

```

#### Exercise 10
Test CatgoryService

```java
@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    CategoryMapper cattegoryMapper;
    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Test
    public void findProducts() {
        List<Category> categoryList = new ArrayList<Category>();
        Category category = new Category();
        categoryList.add(category);

        when(categoryRepository.findAll()).thenReturn(categoryList);
        List<CategoryDTO> categriesDto = categoryService.findAll();
        assertEquals(categriesDto.size(), 1);
        verify(categoryRepository, times(1)).findAll();
    }
}
```


#### Exercise 11
Add a new controller, ProductController. This controller will serve views interacting 
with resources of type Product.



```java 	
@Controller
@RequestMapping("/product")
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


#### Exercise 12
Add Tymeleaf tags in productList.html view.


```html
<tr th:each="product, stat : ${products}"
    th:class="${stat.odd}? 'table-light':''">
  <td th:text="${product.id}">1</td>
  <td th:text="${product.name}">Product 1</td>
  <td th:text="${product.code}">Code</td>
  <td th:text="${product.reservePrice}">Reserved price</td>
  <td th:text="${product.reservePrice}">Best offer</td>
  <td><a href="#" th:href="@{'/product/edit/' + ${product.id}}"><i class="fa-solid fa-pen"></i></a></td>
  <td><a href="#" th:href="@{'/product/delete/' + ${product.id}}"><i class="fa-solid fa-trash"></i></a></td>
</tr>
```
#### Exercise 13
Modify Product Controller to use ProductService.
Inject the list of products into the Model using the argument of
@RequestMapping method. 

```java 
@Controller
@RequestMapping("/product")
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

#### Exercise 14
Add the endpoints for edit and delete. We will need to add also the list of
categories. All categories will be displayed in the view containing product details.

```java 
CategoryService categoryService;

public ProductController(ProductService productService,  CategoryService categoryService) {
  this.productService = productService;
  this.categoryService = categoryService;
}

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
  return "redirect:/product";
}
```

#### Exercise 15
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


    return "redirect:/product" ;
   }
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

#### Exercise 16
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
