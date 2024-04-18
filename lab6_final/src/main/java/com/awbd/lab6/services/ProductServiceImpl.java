package com.awbd.lab6.services;

import com.awbd.lab6.domain.Info;
import com.awbd.lab6.domain.Product;
import com.awbd.lab6.dtos.ProductDTO;
import com.awbd.lab6.exceptions.ResourceNotFoundException;
import com.awbd.lab6.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
            throw new ResourceNotFoundException("product " + l + " not found");
            //throw new RuntimeException("Product not found!");
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
}
