package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() throws QuestionReadException {
        List<QuestionDto> quest = getQuestionsFromCsv(fileNameProvider.getTestFileName());
        List<Question> allQuestList = new ArrayList<>();
        for (QuestionDto qst : quest) {
            if (!checkCorrectLine(qst)) {
               throw new QuestionReadException("Not correct question or too little answers");
            }
            allQuestList.add(qst.toDomainObject());
        }
        // Использовать CsvToBean
        // https://opencsv.sourceforge.net/#collection_based_bean_fields_one_to_many_mappings
        // Использовать QuestionReadException
        // Про ресурсы: https://mkyong.com/java/java-read-a-file-from-resources-folder/
        return allQuestList;
    }

    private List<QuestionDto> getQuestionsFromCsv(String filename) throws QuestionReadException {
        try (InputStream inputStream = getFileFromResourceAsStream(filename);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){
             return (List<QuestionDto>) new CsvToBeanBuilder(reader)
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
    private boolean checkCorrectLine(QuestionDto qDto) {
        return  ((qDto.getText().length() > 3) && (qDto.getAnswers().size() > 2));
    }

    private InputStream getFileFromResourceAsStream(String filename) throws QuestionReadException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(filename);
        if (inputStream == null) {
            throw new QuestionReadException(String.format("File {0} not found",filename), new RuntimeException());
        } else {
            return inputStream;
        }
    }
}
