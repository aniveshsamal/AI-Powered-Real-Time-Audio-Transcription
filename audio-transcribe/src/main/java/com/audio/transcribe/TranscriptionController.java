package com.audio.transcribe;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@RestController
@RequestMapping("/api/transcribe")
public class TranscriptionController {
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public TranscriptionController(@Value("${spring.ai.openai.api-key}") String apiKey) {
        // Assuming the library now recommends using a Builder
        OpenAiAudioApi openAiAudioApi =
                new OpenAiAudioApi(apiKey);

        this.transcriptionModel = new OpenAiAudioTranscriptionModel(openAiAudioApi);
    }
    @PostMapping
    public ResponseEntity<String> transcribeAudio(@RequestParam ("file") MultipartFile file) throws IOException {
        File tempfile = File.createTempFile("audio",".wav");
        file.transferTo(tempfile);

        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .language("en")
                .temperature(0f)
                .build();


        FileSystemResource audioFile = new FileSystemResource(tempfile);

        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
        AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);

        tempfile.delete();
        return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK);

    }

}
