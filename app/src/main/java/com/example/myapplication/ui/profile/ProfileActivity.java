package com.example.myapplication.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.data.model.GitHubCommit;
import com.example.myapplication.data.model.GitHubUser;
import com.example.myapplication.data.remote.GitHubApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    private TextView textName, textBio, textRepos, textFollowers, textLastCommit;
    private ImageView imageAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        String username = getIntent().getStringExtra("USERNAME");

        if (username != null && !username.isEmpty()) {
            loadUserData(username);
        } else {
            textName.setText("Ошибка: имя не передано");
        }

        findViewById(R.id.buttonBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        imageAvatar = findViewById(R.id.imageAvatar);
        textName = findViewById(R.id.textName);
        textBio = findViewById(R.id.textBio);
        textRepos = findViewById(R.id.textRepos);
        textFollowers = findViewById(R.id.textFollowers);
        textLastCommit = findViewById(R.id.textLastCommit);
    }

    private void loadUserData(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api = retrofit.create(GitHubApi.class);

        // 1. Загружаем профиль
        api.getUser(username).enqueue(new Callback<GitHubUser>() {
            @Override
            public void onResponse(@NonNull Call<GitHubUser> call, @NonNull Response<GitHubUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GitHubUser user = response.body();
                    updateUI(user);
                    loadLastCommit(username);
                } else {
                    textName.setText("Пользователь не найден");
                }
            }

            @Override
            public void onFailure(@NonNull Call<GitHubUser> call, @NonNull Throwable t) {
                textName.setText("Ошибка сети");
            }
        });
    }

    private void updateUI(GitHubUser user) {
        textName.setText(user.name != null ? user.name : user.login);
        textBio.setText(user.bio != null ? user.bio : getString(R.string.no_bio));
        textRepos.setText("Репозитории: " + user.public_repos);
        textFollowers.setText("Подписчики: " + user.followers);

        Glide.with(this)
                .load(user.avatar_url)
                .placeholder(R.drawable.ic_github)
                .into(imageAvatar);
    }

    private void loadLastCommit(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GitHubApi api = retrofit.create(GitHubApi.class);
        api.getEvents(username).enqueue(new Callback<GitHubCommit[]>() {
            @Override
            public void onResponse(@NonNull Call<GitHubCommit[]> call, @NonNull Response<GitHubCommit[]> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (GitHubCommit event : response.body()) {
                        if ("PushEvent".equals(event.type)
                                && event.repo != null
                                && event.created_at != null) {

                            String repoName = event.repo.name != null ? event.repo.name : "неизвестный репозиторий";
                            String message = "Пуш в репозиторий";

                            if (event.payload != null
                                    && event.payload.commits != null
                                    && !event.payload.commits.isEmpty()
                                    && event.payload.commits.get(0).message != null) {
                                message = event.payload.commits.get(0).message;
                            }

                            try {
                                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                                SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("ru"));
                                Date date = input.parse(event.created_at);
                                String formattedDate = output.format(date);

                                textLastCommit.setText("Последний пуш: " + formattedDate + "\n" + message + "\nв " + repoName);
                            } catch (Exception e) {
                                textLastCommit.setText("Пуш: " + message + " (в " + repoName + ")");
                            }
                            return;
                        }
                    }
                    textLastCommit.setText("Пушей не найдено");
                } else {
                    textLastCommit.setText("Ошибка ответа");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<GitHubCommit[]> call, @NonNull Throwable t) {
                textLastCommit.setText("Ошибка: " + t.getMessage());
            }
        });
    }
}