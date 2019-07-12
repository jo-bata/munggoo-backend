package com.mashup.munggoo.highlight;

import com.mashup.munggoo.exception.BadRequestException;
import com.mashup.munggoo.exception.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HighlightServiceTest {
    @Autowired
    private HighlightService highlightService;

    @MockBean
    private HighlightRepository highlightRepository;

    private Long id;

    private Long fileId;

    private List<ReqHighlightDto> reqHighlightDtos;

    private List<Highlight> highlights;

    private List<ResHighlightDto> resHighlightDtos;

    private ReqHighlightDto reqHighlightDto;

    @Before
    public void setUp() {
        id = 1L;
        fileId = 1L;
    }

    @Test
    public void saveHighlights() {
        reqHighlightDtos = new ArrayList<>();
        reqHighlightDtos.add(new ReqHighlightDto(10L, 20L, "안녕", 1));
        reqHighlightDtos.add(new ReqHighlightDto(30L, 40L, "안녕하세요 반갑습니다.", 0));
        highlights = reqHighlightDtos.stream().map(reqHighlightDto -> Highlight.from(fileId, reqHighlightDto)).collect(Collectors.toList());
        given(highlightRepository.saveAll(anyCollection())).willReturn(highlights);
        List<Highlight> savedHighlights = highlightService.save(fileId, reqHighlightDtos);
        assertThat(savedHighlights.size()).isEqualTo(reqHighlightDtos.size());
        assertThat(savedHighlights.get(0).getStartIndex()).isEqualTo(10L);
        assertThat(savedHighlights.get(1).getStartIndex()).isEqualTo(30L);
    }

    @Test(expected = BadRequestException.class)
    public void saveEmptyHighlight() {
        reqHighlightDtos = new ArrayList<>();
        highlightService.save(fileId, reqHighlightDtos);
    }

    @Test
    public void getHighlights() {
        reqHighlightDtos = new ArrayList<>();
        reqHighlightDtos.add(new ReqHighlightDto(10L, 20L, "안녕", 1));
        reqHighlightDtos.add(new ReqHighlightDto(30L, 40L, "안녕하세요 반갑습니다.", 0));
        highlights = reqHighlightDtos.stream().map(reqHighlightDto -> Highlight.from(fileId, reqHighlightDto)).collect(Collectors.toList());
        given(highlightRepository.findByFileId(any())).willReturn(highlights);
        resHighlightDtos = highlightService.getHighlights(fileId);
        assertThat(resHighlightDtos.size()).isEqualTo(reqHighlightDtos.size());
        assertThat(resHighlightDtos.get(0).getStartIndex()).isEqualTo(reqHighlightDtos.get(0).getStartIndex());
        assertThat(resHighlightDtos.get(0).getContent()).isEqualTo(reqHighlightDtos.get(0).getContent());
        assertThat(resHighlightDtos.get(0).getIsImportant()).isEqualTo(Boolean.TRUE);
        assertThat(resHighlightDtos.get(1).getStartIndex()).isEqualTo(reqHighlightDtos.get(1).getStartIndex());
        assertThat(resHighlightDtos.get(1).getContent()).isEqualTo(reqHighlightDtos.get(1).getContent());
        assertThat(resHighlightDtos.get(1).getIsImportant()).isEqualTo(Boolean.FALSE);
    }

    @Test(expected = NotFoundException.class)
    public void getEmptyHighlight() {
        highlights = new ArrayList<>();
        given(highlightRepository.findByFileId(any())).willReturn(highlights);
        highlightService.getHighlights(fileId);
    }

    @Test
    public void deleteHighlight() {
        reqHighlightDto = new ReqHighlightDto(10L, 20L, "안녕", 1);
        given(highlightRepository.findById(any())).willReturn(Optional.of(Highlight.from(fileId, reqHighlightDto)));
        Highlight highlight = highlightService.deleteHighlight(id);
        assertThat(highlight.getStartIndex()).isEqualTo(reqHighlightDto.getStartIndex());
        assertThat(highlight.getType()).isEqualTo(HighlightType.WORD);
        assertThat(highlight.getIsImportant()).isEqualTo(Boolean.TRUE);
    }

    @Test(expected = NotFoundException.class)
    public void deleteEmptyHighlight() {
        given(highlightRepository.findById(any())).willReturn(Optional.empty());
        highlightService.deleteHighlight(id);
    }
}
