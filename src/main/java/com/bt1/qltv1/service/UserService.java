package com.bt1.qltv1.service;

import com.bt1.qltv1.criteria.BaseCriteria;
import com.bt1.qltv1.criteria.UserCriteria;
import com.bt1.qltv1.dto.ListOutputResult;
import com.bt1.qltv1.dto.book.UploadImageResponse;
import com.bt1.qltv1.dto.user.ProfileResponse;
import com.bt1.qltv1.dto.user.UserDTO;
import com.bt1.qltv1.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UserService {
    ProfileResponse findById(long id);

    ListOutputResult findAllUser(UserCriteria userCriteria, BaseCriteria baseCriteria, Pageable pageable);

    void importUserByCsv(MultipartFile file);

    ProfileResponse save(UserDTO dto);

    void deleteUser(long id);

    User findFirstByEmail(String email);

    UploadImageResponse updateAvatar(MultipartFile avatar);

    void deleteAvatar();

    UploadImageResponse getAvatar();

    ProfileResponse getProfile();
}
