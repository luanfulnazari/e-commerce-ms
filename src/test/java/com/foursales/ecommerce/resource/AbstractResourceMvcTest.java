package com.foursales.ecommerce.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foursales.ecommerce.config.TestSecurityConfig;
import com.foursales.ecommerce.service.AuthService;
import com.foursales.ecommerce.service.OrderService;
import com.foursales.ecommerce.service.ProductService;
import com.foursales.ecommerce.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = TestSecurityConfig.class)
public abstract class AbstractResourceMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected OrderService orderService;

    @MockitoBean
    protected ProductService productService;

    @MockitoBean
    protected ReportService reportService;
}