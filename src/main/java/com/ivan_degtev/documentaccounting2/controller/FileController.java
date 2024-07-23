package com.ivan_degtev.documentaccounting2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivan_degtev.documentaccounting2.dto.file.FileEntityDTO;
import com.ivan_degtev.documentaccounting2.dto.fileEntity.FileEntityParamsDTO;
import com.ivan_degtev.documentaccounting2.mapper.FileEntityMapper;
import com.ivan_degtev.documentaccounting2.model.FileEntity;
import com.ivan_degtev.documentaccounting2.service.impl.FileServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/files")
@AllArgsConstructor
public class FileController {

    private FileServiceImpl fileService;
    private FileEntityMapper fileEntityMapper;
//    @GetMapping(path = "")
//    public ResponseEntity<List<FileEntity>> getFiles() {
//        List<FileEntity> fileEntities = fileService.findAll();
//        return ResponseEntity.ok()
//                .body(fileEntities);
//    }

    @GetMapping(path = "")
    public ResponseEntity<List<FileEntityDTO>> getAllFiles() {
//        List<FileEntity> files = fileService.findAll();
//        return files.stream()
//                .map(file -> new FileEntityDTO(
//                        file.getId(),
//                        file.getFilename(),
//                        file.getFiletype(),
//                        file.getAuthor().getUsername(),
//                        file.getData(),
//                        file.getPublicEntity(),
//                        file.getAvailableFor(),
//                        file.getCreationDate(),
//                        file.getUpdateDate()
//                ))
//                .collect(Collectors.toList());
        List<FileEntityDTO> fileEntityDTOS =  fileService.getAll();
        return ResponseEntity.ok().body(fileEntityDTOS);
    }

    // делает миниатюру файла, используется на страницах с инфой о файле и на общей странице
    @GetMapping(path = "/{id}/thumbnail")
    public ResponseEntity<byte[]> getFileThumbnail(@PathVariable Long id) {
        FileEntity fileEntity = fileService.getFile(id);
        byte[] thumbnailData = fileService.generateThumbnail(fileEntity.getData(), fileEntity.getFiletype());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok()
                .headers(headers)
                .body(thumbnailData);
    }


    @PostMapping("/upload")
    public FileEntity uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestPart("params") String paramsJson
    ) throws IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        FileEntityParamsDTO paramsDTO = objectMapper.readValue(paramsJson, FileEntityParamsDTO.class);

        log.info("получили файл с параметрами {}", file.getOriginalFilename());
        log.info("получили дто с  параметрами {}", paramsDTO.toString());
        return fileService.storeFile(file, paramsDTO);
    }

    // используется на странице с информацией о текущем файле
    @GetMapping(path = "show_data_file/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)" +
            "or @userUtils.currentFileEntityIsPublicOrAvailableForFileEntity(#id)")
    public ResponseEntity<FileEntityDTO> showDataFile(@PathVariable Long id) {
        FileEntityDTO fileEntityDTO = fileService.getDataFile(id);
        return ResponseEntity.ok()
                .body(fileEntityDTO);
    }


    // используется для просмотра во весь экран в браузере или скачивания файла(зависит от типа файла)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)" +
            "or @userUtils.currentFileEntityIsPublicOrAvailableForFileEntity(#id)")
    public ResponseEntity<byte[]> showFile(
            @PathVariable Long id,
            @RequestParam(required = false) boolean download
    )
            throws UnsupportedEncodingException
    {
        FileEntity fileEntity = fileService.getFile(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileEntity.getFiletype()));

        String dispositionType = download ? "attachment" : "inline";
        String encodedFilename = URLEncoder.encode(fileEntity.getFilename(), StandardCharsets.UTF_8).replace("+", "%20");
        String contentDisposition = String.format("%s; filename=\"%s\"", dispositionType, encodedFilename);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileEntity.getData());
    }

    @PutMapping(path = "{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MODERATOR') or @userUtils.currentUserIsAuthorForFiles(#id)")
    public ResponseEntity<FileEntityDTO> updateFile(
            @RequestBody FileEntityParamsDTO fileEntityUpdateDTO,
            @PathVariable Long id
    ) {
        log.info("получил с фронта дто с изменениями {}", fileEntityUpdateDTO.toString());
        FileEntityDTO fileEntityDTO = fileService.update(fileEntityUpdateDTO, id);
        log.info("в сервисе все отработало, получил обратно общую дто с изменённой сущностью {}", fileEntityDTO.toString());
        return ResponseEntity.ok().body(fileEntityDTO);
    }


    @DeleteMapping(path = "{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) throws FileNotFoundException {
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}

