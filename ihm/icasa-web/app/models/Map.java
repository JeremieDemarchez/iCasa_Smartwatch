/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package models;

import java.lang.String;
import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

/**
 * @author Thomas Leveque
 */
@Entity
public class Map extends Model {

    @Id
    public String id;

    public String name;

    public String description;

    public String gatewayURL;

    public String imgURL;

    public static Finder<String,Map> find = new Finder<String,Map>(
            String.class, Map.class
    );

    public String getName() {
        return this.name;
    }
}
