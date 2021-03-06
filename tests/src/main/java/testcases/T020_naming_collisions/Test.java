/*
 * Copyright (c) 2018-2019 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package testcases.T020_naming_collisions;

import static com.google.common.truth.Truth.assertThat;

public class Test {

    public static void run() {
        Foo.Parent parent = new FooGrandparentImpl().p();
        Foo.ChildA a = parent.a();
        Foo.ChildB b = parent.b();
        Foo.ChildC c = parent.c();
        Foo.ChildD d = parent.d();

        assertThat(a.string()).isEqualTo("a");
        assertThat(b.string()).isEqualTo("b");
        assertThat(c.c()).isNotNull();
        assertThat(d.d()).isNotNull();
    }
}
