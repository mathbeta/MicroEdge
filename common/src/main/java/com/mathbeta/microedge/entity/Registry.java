package com.mathbeta.microedge.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * docker镜像仓库
 *
 * @author xuxiuyou
 * @date 2023/9/22 11:13
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Registry {
    private String id;
    private String url;
    private boolean isPublic;
    private boolean secure;
    private String certificate;
    private boolean requiresCertificate;
    private String userName;
    private String password;
    private String userEmail;
}
