/*
 * Copyright 2020-2099 sa-token.cc
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
package devilSpiderX.server.webServer.module.satoken.annotation;

import cn.dev33.satoken.annotation.SaCheckLogin;
import devilSpiderX.server.webServer.module.satoken.StpKit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 登录认证校验：只有登录之后才能进入该方法。
 *
 * <p> 可标注在方法、类上（效果等同于标注在此类的所有方法上）
 *
 * @author click33
 * @since 1.10.0
 */
@SaCheckLogin(type = StpKit.ADMIN_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SaAdminCheckLogin {
}
