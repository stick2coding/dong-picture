package com.dong.picture.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dong.picture.model.dto.picture.PictureQueryRequest;
import com.dong.picture.model.dto.picture.PictureReviewRequest;
import com.dong.picture.model.dto.picture.PictureUploadRequest;
import com.dong.picture.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dong.picture.model.entity.User;
import com.dong.picture.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author sunbin
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-02-17 16:17:36
*/
public interface PictureService extends IService<Picture> {
    /**
     * 获取查询包装类
     * @param pictureQueryRequest
     * @return
     */
    QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取查询结果
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    void validPicture(Picture picture);

    /**
     * 分页获取图片封装
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getpictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片上传
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser);

    /**
     * 图片审核
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 完善图片审核信息
     * 图片上传（创建、用户编辑、管理员更新均需要进行补充审核状态）
     * @param picture
     * @param loginUser
     */
    void fillPictureReviewParams(Picture picture, User loginUser);

}
