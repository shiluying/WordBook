package com.shiluying.wordbook.Server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NewsServer {
    public ArrayList<String> getNewsList(String text){
        ArrayList<String> result=new ArrayList<String>();
        Document document=Jsoup.parse(text);
        if (document!=null) {
            Elements elements=document.getElementsByClass("span10");
            for(Element e:elements){
                if(e!=null){
                    Elements tags=e.getElementsByTag("a");
                    for(Element tag:tags){
                        result.add(tag.attr("href").toString());
                    }
                }
            }
        }
        return result;
    }
    public ArrayList<String> getContents(String html){
        ArrayList<String> result=new ArrayList<String>();
        Document document=Jsoup.parse(html);
        if (document!=null) {
            Elements title=document.getElementsByTag("h3");
            if(title.size()>0){
                result.add(title.get(0).text());
            }else{
                result.add("");
            }
            Elements elements=document.getElementsByClass("span12");
            for(Element e:elements){
                if(e!=null){
                    Elements pullElements=e.getElementsByClass("pull-left");
                    Element pull = pullElements.get(0);
                    pull.text("");
                    String content = e.toString();
                    Elements children =e.children();
                    for(Element child :children){
                        String regex = child.toString();
                        content=content.replaceAll(regex, "");
                    }
                    Document doc = Jsoup.parse(content);
                    result.add(doc.text());
                }else{
                    result.add("");
                }
            }
        }
        return result;
    }

}
