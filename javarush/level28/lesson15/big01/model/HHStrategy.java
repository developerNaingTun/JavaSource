package com.javarush.test.level28.lesson15.big01.model;

import com.javarush.test.level28.lesson15.big01.vo.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zlayl on 04.09.2016.
 * Zlaylink@gmail.com
 */
public class HHStrategy implements Strategy
{
    private static final String URL_FORMAT = "http://hh.ru/search/vacancy?text=java+%s&page=%d";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";
    private static final String REFERRER = "";
    private static final int TIMEOUT = 5000;

    @Override
    public List<Vacancy> getVacancies(String searchString)
    {
        List<Vacancy> vacancies = new ArrayList<>();
        int page = 0;
        try
        {
            while (true)
            {
                Document document = getDocument(searchString, page);
                Elements elements = document.select("[data-qa=vacancy-serp__vacancy]");
                if (elements.isEmpty()) break;

                for (Element element : elements)
                {
                    Vacancy vacancy = new Vacancy();

                    Element title = element.select("[data-qa=vacancy-serp__vacancy-title]").first();
                    vacancy.setTitle(title.text());
                    vacancy.setUrl(title.attr("href"));

                    Element city = element.select("[data-qa=vacancy-serp__vacancy-address]").first();
                    vacancy.setCity(city.text());

                    Element companyName = element.select("[data-qa=vacancy-serp__vacancy-employer]").first();
                    vacancy.setCompanyName(companyName.text());

                    vacancy.setSiteName("http://hh.ru");

                    Element salary = element.select("[data-qa=vacancy-serp__vacancy-compensation]").first();
                    vacancy.setSalary(salary != null ? salary.text() : "");

                    vacancies.add(vacancy);
                }
                page++;
            }
        }
        catch (IOException ignored) {
        }
        return vacancies;
    }

    protected Document getDocument(String searchString, int page) throws IOException
    {

        String URL = String.format(URL_FORMAT, URLEncoder.encode(searchString, "UTF-8"), page);
        return Jsoup.connect(URL).userAgent(USER_AGENT).referrer(REFERRER).timeout(TIMEOUT).get();
    }
}

