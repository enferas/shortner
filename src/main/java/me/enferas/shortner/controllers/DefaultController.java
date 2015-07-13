package me.enferas.shortner.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.enferas.entities.ShortnerEntities;
import me.enferas.entities.ShortnerUtil;
import me.enferas.entities.RealURL;
import me.enferas.entities.ShortURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultController {
    
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String index() {
       
       return "index";
   }
   
   @RequestMapping(value = "/{surl}", method = RequestMethod.GET)
   public String redirect(HttpServletRequest request, @PathVariable final String surl) {

       if (surl == null || "".equals(surl))   
           return "index";
       
       ShortnerEntities.build_DB();
       // query realURL
       ShortURL shorturl = ShortnerUtil.getDefault().getShortUrl(surl);
       
       if (shorturl == null)
           return "notfound";
       
       // redirect to realURL
       //response.setHeader("Location", shorturl.getRealUrl().getUrl());
       String redirectUrl = shorturl.getRealUrl().getUrl();
       if (!redirectUrl.toLowerCase().startsWith("http://") || !redirectUrl.toLowerCase().startsWith("https://"))
           redirectUrl = "http://" + redirectUrl;
        return "redirect:" + redirectUrl;
   }
   
   @RequestMapping(value = "/add", method = RequestMethod.POST)
   public String add(HttpServletRequest request)
   {
       String surl = request.getParameter("surl");
       String rurl = request.getParameter("rurl");
       
       // check if short exists
       ShortnerEntities.build_DB();
       RealURL realurl = new RealURL();
       realurl.setUrl(rurl);
       
       realurl = ShortnerUtil.getDefault().addRealUrl(realurl);
       
       ShortURL shorturl = new ShortURL();
       shorturl.setUrl(surl);
       shorturl.setRealUrl(realurl);
       
       ShortnerUtil.getDefault().addShortUrl(shorturl);
              
       return "redirect:/";
   }
    
}