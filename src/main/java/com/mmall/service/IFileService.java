package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jianl on 2018/3/31.
 */
public interface IFileService {

    public abstract String upload(MultipartFile file, String path);

}
