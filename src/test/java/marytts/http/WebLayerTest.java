/**
 * Copyright 2015 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package marytts.http;

import marytts.http.controllers.MaryController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * MaryTTS web layer testing class
 *
 * @author <a href="mailto:attashah@coli.uni-saarland.de">Atta-Ur-Rehman
 * Shah</a>
 */
@RunWith(SpringRunner.class)
@WebMvcTest(MaryController.class)
//@AutoConfigureRestDocs(outputDir = "build/snippets")
public class WebLayerTest {

    @Rule
    public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("build/snippets");

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private RestDocumentationResultHandler document;

    @Before
    public void setUp() {
        this.document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                       .apply(documentationConfiguration(this.restDocumentation).uris()
                              .withPort(59125))
                       .alwaysDo(this.document)
                       .build();
    }

    @Test
    public void getConfiguration() throws Exception {
        this.mockMvc.perform(get("/getConfiguration").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().string("ok"));
    }

    @Test
    public void setConfiguration() throws Exception {

        this.document.document(
            requestParameters(
                parameterWithName("configuration").description("The new configuration value")
            )
        );
        this.mockMvc.perform(post("/setConfiguration")
                             .param("configuration", "This is a configuration example.")
                             .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }

    @Test
    public void setLoggerLevel() throws Exception {

        this.document.document(
            requestParameters(
                parameterWithName("level").description("The new logging level e.g. DEBUG")
            )
        );

        this.mockMvc.perform(get("/setLoggerLevel")
                             .param("level", "DEBUG")
                             .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    }
}
