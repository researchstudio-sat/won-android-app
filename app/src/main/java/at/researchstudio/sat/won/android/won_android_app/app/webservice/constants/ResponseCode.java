/*
 * Copyright 2015 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.webservice.constants;

public abstract class ResponseCode {
    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_NOUSER = 1;
    public static final int CONNECTION_ERR = 2;
    public static final int REGISTER_SUCCESS = 3;
    public static final int REGISTER_USEREXISTS = 4;
    public static final int LOGOUT_SUCCESS = 5;
}
