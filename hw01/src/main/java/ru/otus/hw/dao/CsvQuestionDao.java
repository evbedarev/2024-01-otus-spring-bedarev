package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() throws QuestionReadException {
        Resource resource = new ClassPathResource(fileNameProvider.getTestFileName());
        List<QuestionDto> quest = getQuestionsFromCsv(resource);
        List<Question> allQuestList = new ArrayList<>();
        for (QuestionDto qst : quest) {
            if (!checkCorrectLine(qst)) {
               throw new QuestionReadException("Not correct question or too little answers", new RuntimeException());
            }
            allQuestList.add(qst.toDomainObject());
        }
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/

        return allQuestList;
    }

    private List<QuestionDto> getQuestionsFromCsv(Resource resource) throws QuestionReadException {
        try {
            return (List<QuestionDto>) new CsvToBeanBuilder(new FileReader(resource.getFile()))
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();
        } catch (IOException e) {
            throw new QuestionReadException(String.format("Problem read file question or File {0} not found",
                    fileNameProvider.getTestFileName()), e);
        }
    }
    private Boolean checkCorrectLine(QuestionDto qDto) {
        return  ((qDto.getText().length() > 3) && (qDto.getAnswers().size() > 2));
    }
}
