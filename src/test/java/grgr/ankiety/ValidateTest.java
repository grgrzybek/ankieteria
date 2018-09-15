/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package grgr.ankiety;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidateTest {

    public static Logger LOG = LoggerFactory.getLogger(ValidateTest.class);

    @Test
    public void validate() throws IOException {
        int ln = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/dane.txt"), StandardCharsets.UTF_8))) {
            S st = S.KLASA;
            String line = null;
            final int[] lastPoint = { 12 };
            while ((line = reader.readLine()) != null) {
                ln++;
                switch (st) {
                    case KLASA: {
                        String[] r = line.split(" ");
                        assertThat(r).withFailMessage("line " + ln).hasSize(3);
                        assertThat(r[0]).matches("^[km]$");
                        assertThat(r[1]).matches("^[1234]$");
                        assertThat(r[2]).withFailMessage("line " + ln).matches("^[1-5]$");
                        int klasa = Integer.parseInt(r[2]);
                        lastPoint[0] = klasa >= 1 && klasa < 4 ? 12 : 13;
                        st = S.P1;
                        break;
                    }
                    case P1: {
                        String[] r = line.split(" ");
                        assertThat(r.length).withFailMessage("line " + ln).isGreaterThanOrEqualTo(15);
                        for (int i = 0; i < 15; i++) {
                            assertThat(r[i]).matches("^[1-5-]$");
                        }
                        st = S.P2;
                        break;
                    }
                    case P2: {
                        String[] r = line.split(",");
                        assertThat(r.length).withFailMessage("line " + ln).isGreaterThanOrEqualTo(12);
                        for (int i = 0; i < 11; i++) {
                            String[] s = r[i].split(" ");
                            assertThat(s).hasSize(3);
                        }
                        assertThat(r[11].split(" ").length).withFailMessage("line " + ln).isGreaterThanOrEqualTo(3);
                        for (int i = 0; i < 12; i++) {
                            String[] s = r[i].split(" ");
                            for (int j = 0; j < 3; j++) {
                                assertThat(s[j]).withFailMessage("line " + ln).matches("^[1-5-]$");
                            }
                        }
                        st = S.P3;
                        break;
                    }
                    case P3: {
                        if (line.startsWith("1")) {
                            assertThat(line.substring(2).length()).isGreaterThan(0);
                            assertThat(line.charAt(1)).withFailMessage("line " + ln).isEqualTo(' ');
                        } else {
                            assertThat(line).withFailMessage("line " + ln).isEqualTo("2");
                        }
                        st = S.P4;
                        break;
                    }
                    case P4: {
                        if (!"-".equals(line)) {
                            assertThat(line.split(" ")).withFailMessage("line " + ln).allSatisfy(s -> assertThat(Integer.parseInt(s)).isBetween(1, lastPoint[0]));
                        } else {
                            assertThat(line).isEqualTo("-");
                        }
                        st = S.P5;
                        break;
                    }
                    case P5: {
                        if (!"-".equals(line)) {
                            assertThat(line.split(" ")).allSatisfy(s -> assertThat(Integer.parseInt(s)).isBetween(1, lastPoint[0]));
                        } else {
                            assertThat(line).isEqualTo("-");
                        }
                        st = S.P6;
                        break;
                    }
                    case P6: {
                        if (!"-".equals(line)) {
                            assertThat(line.split(" ")).withFailMessage("line " + ln).allSatisfy(s -> assertThat(Integer.parseInt(s)).isBetween(1, lastPoint[0]));
                        } else {
                            assertThat(line).isEqualTo("-");
                        }
                        st = S.P7;
                        break;
                    }
                    case P7: {
                        if ("".equals(line)) {
                            st = S.KLASA;
                        } else {
                            if (!"-".equals(line)) {
                                assertThat(line.length()).isGreaterThan(0);
                            } else {
                                assertThat(line).isEqualTo("-");
                            }
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Problem w linii {}", ln);
            throw e;
        }
    }

    public enum S {
        KLASA,         // płeć, wiek, klasa
        P1,            // gorsze traktowanie
        P2,            // formy zachowania
        P3,            // czy słyszałeś/aś o akcjach?
        P4,            // o których akcjach słyszałaś?
        P5,            // które są szczególnie potrzebne?
        P6,            // w których dziecko brało udział?
        P7             // uwagi
    }

}
