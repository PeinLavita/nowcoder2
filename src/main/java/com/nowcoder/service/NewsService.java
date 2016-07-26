package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Created by yby on 2016/7/3.
 */

@Service
public class NewsService {

    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit) {
        return newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public String saveImage(MultipartFile file) throws IOException{
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0){
            return null;
        }

        String fileExt = file.getOriginalFilename().substring(dotPos+1);
        if (!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }

        String fileName = UUID.randomUUID().toString().replaceAll("-", "")+"."+fileExt;
        Files.copy(file.getInputStream(),
                new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return  ToutiaoUtil.TOUTIAO_DOMAIN + "image?name=" + fileName;
    }

    public int addNews(News news){
        newsDAO.addNews(news);
        return news.getId();
    }

    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }

    public void updateCommentCount(int newsId, int count){
        newsDAO.updateCommentCount(newsId, count);
    }

    public void updateLikeCount(int id, int count) {
        newsDAO.updateLikeCount(id, count);
    }

}
