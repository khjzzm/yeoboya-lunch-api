package com.yeoboya.lunch.api.v1.event.service;

import com.yeoboya.lunch.api.v1.common.response.Code;
import com.yeoboya.lunch.api.v1.common.response.Response;
import com.yeoboya.lunch.api.v1.event.domain.Banner;
import com.yeoboya.lunch.api.v1.event.repository.BannerRepository;
import com.yeoboya.lunch.api.v1.event.reqeust.BannerRequest;
import com.yeoboya.lunch.api.v1.event.response.BannerFileResponse;
import com.yeoboya.lunch.api.v1.event.response.BannerResponse;
import com.yeoboya.lunch.api.v1.file.constant.Directory;
import com.yeoboya.lunch.api.v1.file.domain.BannerFile;
import com.yeoboya.lunch.api.v1.file.response.FileResponse;
import com.yeoboya.lunch.api.v1.file.service.FileServiceS3;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final FileServiceS3 fileServiceS3;
    private final BannerRepository bannerRepository;
    private final Response response;

    public ResponseEntity<Response.Body> getBanners(Optional<Banner.DisplayLocation> displayLocation) {

        LocalDateTime now = LocalDateTime.now();
        List<Banner> banners;
        if (displayLocation.isPresent()) {
            banners = bannerRepository.findAllByDisplayLocationAndDateRange(displayLocation.get(), now);
        } else {
            banners = bannerRepository.findAllByDateRange(now);
        }

        List<BannerResponse> bannerResponses = banners.stream()
                .map(banner -> new BannerResponse(
                        banner.getId(),
                        banner.getTitle(),
                        banner.getDisplayOrder(),
                        banner.getStartDate(),
                        banner.getEndDate(),
                        banner.getDisplayLocation().name(),
                        banner.getBannerFiles().stream().map(file ->
                                new BannerFileResponse(file.getId(), file.getOriginalFileName(),
                                        file.getFileName(), file.getFilePath(), file.getExtension(),
                                        file.getSize())).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return response.success(Code.SEARCH_SUCCESS, bannerResponses);
    }


    @Transactional
    public ResponseEntity<Response.Body> saveBanner(MultipartFile file, BannerRequest bannerRequest) {

        BannerFile boardFile = null;
        if (file != null && !file.isEmpty()) {
//            try {
                FileResponse upload = fileServiceS3.upload(file, Directory.BANNER, Function.identity());
//                boardFile = BannerFile.builder().fileUploadResponse(upload).build();
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to upload file", e);
//            }
        }

        Banner banner = Banner.createBanner(
                bannerRequest.getTitle(),
                bannerRequest.getDisplayOrder(),
                bannerRequest.getStartDate(),
                bannerRequest.getEndDate(),
                bannerRequest.getDisplayLocation(),
                boardFile
        );

        bannerRepository.save(banner);
        return response.success(Code.SAVE_SUCCESS);
    }

    public ResponseEntity<Response.Body> deleteBanner(Long id) {
        bannerRepository.deleteById(id);
        return response.success(Code.DELETE_SUCCESS);
    }
}
