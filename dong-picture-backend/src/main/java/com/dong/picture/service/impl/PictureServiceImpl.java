package com.dong.picture.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dong.picture.exception.BusinessException;
import com.dong.picture.exception.ErrorCode;
import com.dong.picture.exception.ThrowUtils;
import com.dong.picture.manager.FileManager;
import com.dong.picture.model.dto.file.UploadPictureResult;
import com.dong.picture.model.dto.picture.PictureQueryRequest;
import com.dong.picture.model.dto.picture.PictureReviewRequest;
import com.dong.picture.model.dto.picture.PictureUploadRequest;
import com.dong.picture.model.entity.Picture;
import com.dong.picture.model.entity.User;
import com.dong.picture.model.enums.PictureReviewStatusEnum;
import com.dong.picture.model.vo.PictureVO;
import com.dong.picture.model.vo.UserVO;
import com.dong.picture.service.PictureService;
import com.dong.picture.mapper.PictureMapper;
import com.dong.picture.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author sunbin
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-02-17 16:17:36
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Autowired
    FileManager fileManager;

    @Autowired
    UserService userService;

    /**
     * 将查询请求转为查数据库的请求对象
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();

        if (pictureQueryRequest == null) {
            return queryWrapper;
        }

        // 从请求中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        // 增加审核字段
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        String reviewMessage = pictureQueryRequest.getReviewMessage();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText)
                    .or()
                    .like("introduction", searchText)
            );
        }
        queryWrapper.eq(ObjUtil.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        queryWrapper.eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth);
        queryWrapper.eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight);
        queryWrapper.eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize);
        queryWrapper.eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);
        // 增加审核字段查询
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
        queryWrapper.eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId);
        queryWrapper.like(StrUtil.isNotEmpty(reviewMessage), "reviewMessage", reviewMessage);
        return queryWrapper;
    }

    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        // 转换
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0){
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUser(userVO);
        }
        return pictureVO;
    }

    @Override
    public Page<PictureVO> getpictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(picturePage.getCurrent(), picturePage.getSize(), picturePage.getTotal());
        if (CollUtil.isEmpty(pictureList)){
            return pictureVOPage;
        }

        // 转换，注意这里这种方法会循环获取用户信息，不可取，应该修改为一次性湖区用户信息
//        List<PictureVO> pictureVOList = pictureList.stream().map(picture -> {
//            PictureVO pictureVO = getPictureVO(picture, request);
//            return pictureVO;
//        }).collect(Collectors.toList());
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        // 拿到所有的用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        // 这里用map主要是为了后面匹配的时候方便
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 遍历原数据，填充用户信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }


    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }



    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 上传图片前需要先判断是新上传还是更新
        Long pictureId = null;
        if (pictureUploadRequest != null) {
            pictureId = pictureUploadRequest.getId();
        }
        // 如果有图片ID，需要先校验图片在数据库中是否是否存在
        if (pictureId != null) {
//            Boolean exists = this.lambdaQuery().eq(Picture::getId, pictureId).exists();
//            ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND, "图片不存在");

            // 先判断图片是否存在
            Picture oldPicture = this.getById(pictureId);
            ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND, "图片不存在");
            // 增加了权限限制，只能本人或者管理员才可编辑（如果不是本人，还不是管理员，那就不能更改）
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        // 前置校验完成，开始上传，并返回信息
        // 这里的存储路径以用户为单位进行隔离
        String uploadPathPrefix = String.format("public/%s", loginUser.getId());
        UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, uploadPathPrefix);
        // 构造存入数据库的信息
        Picture picture = new Picture();
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setUserId(loginUser.getId());

        // 这里要完善审核信息相关字段
        this.fillPictureReviewParams(picture, loginUser);

        // 判断是更新还是新建
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(new Date());
        }

        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
        return PictureVO.objToVo(picture);
    }

    @Override
    public void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 拿到待审核的图片ID
        long id = pictureReviewRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 要更新的状态
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        // 拿到对应的枚举信息，后续要判断如果拿到的不正确，需要抛出异常
        PictureReviewStatusEnum reviewStatusEnum = PictureReviewStatusEnum.getEnumByValue(reviewStatus);
        ThrowUtils.throwIf(reviewStatusEnum == null, ErrorCode.PARAMS_ERROR);
        // 审核操作，不能把审核中的状态再更新为审核中
        ThrowUtils.throwIf(reviewStatusEnum == PictureReviewStatusEnum.REVIEWING,
                ErrorCode.PARAMS_ERROR,
                "图片正在审核中，请勿重复操作");

        // 判断图片是否存在
        Picture picture = this.getById(id);
        ThrowUtils.throwIf(picture == null, ErrorCode.NOT_FOUND, "图片不存在");

        // 如果当前图片的状态已经和要更新的状态一致，就报错
        if (picture.getReviewStatus().equals(reviewStatus)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片状态已更新，请勿重复操作");
        }

        // 更新图片状态(只更新审核相关，其他不管)
        Picture updatePicture = new Picture();
        BeanUtils.copyProperties(pictureReviewRequest, updatePicture);
        updatePicture.setReviewerId(loginUser.getId());
        updatePicture.setReviewTime(new Date());
        boolean result = this.updateById(updatePicture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片审核失败");
    }

    /**
     * 填充审核信息
     * @param picture
     * @param loginUser
     */
    @Override
    public void fillPictureReviewParams(Picture picture, User loginUser) {
        // 如果是管理员操作，自动通过审核
        if (userService.isAdmin(loginUser)) {
            picture.setReviewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setReviewMessage("管理员审核通过");
            picture.setReviewerId(loginUser.getId());
            picture.setReviewTime(new Date());
        } else {
            // 不是管理员，那么操作后，状态均改为待审核
            picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getValue());
        }

    }
}




