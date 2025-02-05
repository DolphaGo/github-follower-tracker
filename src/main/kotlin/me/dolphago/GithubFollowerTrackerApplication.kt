package me.dolphago

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
class GithubFollowerTrackerApplication

fun main(args: Array<String>) {
    SpringApplication.run(GithubFollowerTrackerApplication::class.java, *args)
}
