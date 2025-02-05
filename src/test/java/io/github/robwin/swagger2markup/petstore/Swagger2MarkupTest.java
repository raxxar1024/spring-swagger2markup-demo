/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package io.github.robwin.swagger2markup.petstore;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.robwin.swagger2markup.SwaggerConfig;
import io.github.robwin.swagger2markup.petstore.model.Category;
import io.github.robwin.swagger2markup.petstore.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureRestDocs(outputDir = "build/asciidoc/snippets")
@SpringBootTest(classes = {Application.class, SwaggerConfig.class})
@AutoConfigureMockMvc
public class Swagger2MarkupTest {

//    private static final String SWAGGER_URL = "http://localhost:8085/v2/api-docs?group=1.restful_web";
    private static final String SWAGGER_URL = "http://114.116.241.44:9999/iotdm/v2/api-docs";


    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addANewPetToTheStore() throws Exception {
        this.mockMvc.perform(post("/pets/").content(createPet())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("addPetUsingPOST", preprocessResponse(prettyPrint())))
                .andExpect(status().isOk());
    }

    @Test
    public void createSpringfoxSwaggerJson() throws Exception {
        HttpResponse res = HttpRequest.get(SWAGGER_URL).execute();
        String swaggerJson = res.body();

        String outputDir = System.getProperty("io.springfox.staticdocs.outputDir");
        Files.createDirectories(Paths.get(outputDir));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputDir, "swagger.json"), StandardCharsets.UTF_8)){
            writer.write(swaggerJson);
        }
    }

    private String createPet() throws JsonProcessingException {
        Pet pet = new Pet();
        pet.setId(1l);
        pet.setName("Wuffy");
        Category category = new Category(1l, "Hund");
        pet.setCategory(category);
        return new ObjectMapper().writeValueAsString(pet);
    }
}
