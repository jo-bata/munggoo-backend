package com.mashup.munggoo.highlight;

import com.mashup.munggoo.exception.BadRequestException;
import com.mashup.munggoo.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class HighlightService {
    private final HighlightRepository highlightRepository;

    public List<Highlight> save(Long fileId, List<ReqHighlightDto> reqHighlightDtos) {
        if (reqHighlightDtos.isEmpty()) {
            throw new BadRequestException("Request Body Is Empty.");
        }
        return highlightRepository.saveAll(reqHighlightDtos.stream()
                .map(reqHighlightDto -> Highlight.from(fileId, reqHighlightDto))
                .collect(Collectors.toList()));
    }

    public List<ResHighlightDto> getHighlights(Long fileId) {
        List<Highlight> highlights = highlightRepository.findByFileId(fileId);
        if (highlights.isEmpty()) {
            throw new NotFoundException("Highlight Does Not Exist.");
        }
        return highlights.stream().map(ResHighlightDto::new).collect(Collectors.toList());
    }

    public Highlight deleteHighlight(Long id) {
        Optional<Highlight> highlight = highlightRepository.findById(id);
        if (highlight.isPresent()) {
            highlightRepository.deleteById(id);
            return highlight.get();
        } else {
            throw new NotFoundException("Highlight Does Not Exist.");
        }
    }
}
