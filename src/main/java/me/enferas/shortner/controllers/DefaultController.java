package me.enferas.shortner.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.Math.random;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.enferas.entities.Click;
import me.enferas.entities.DeviceType;
import me.enferas.entities.ShortnerEntities;
import me.enferas.entities.ShortnerUtil;
import me.enferas.entities.RealURL;
import me.enferas.entities.ShortURL;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

@Controller
public class DefaultController {

    @Autowired
    ServletContext context;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model) {

        model.addAttribute("LongURL", "");
        model.addAttribute("ShortURL", "");
        model.addAttribute("LinkShortner", "");
        model.addAttribute("LinkDataShortner", "");
        model.addAttribute("Error", "");
        return "index";
    }

    @RequestMapping(value = "/{surl}/notFound", method = RequestMethod.GET)
    public String notFound(Model model) {
        return "notFound";
    }

    @RequestMapping(value = "/{surl}", method = RequestMethod.GET)
    public String redirect(HttpServletRequest request, Device device, @PathVariable final String surl) {

        if (surl == null || "".equals(surl)) {
            return "index";
        }

        ShortnerEntities.build_DB();
        // query realURL
        ShortURL shorturl = ShortnerUtil.getDefault().getShortUrl(surl);

        if (shorturl == null) {
            return "notFound";
        }

        DeviceType deviceType = DeviceType.UNKNOWN;

        if (device.isNormal()) {
            deviceType = DeviceType.DESKTOP;
        } else if (device.isMobile()) {
            deviceType = DeviceType.MOBILE;
        } else if (device.isTablet()) {
            deviceType = DeviceType.TABLET;
        }

        Click click = new Click();
        click.setIP(request.getRemoteAddr());
        click.setClicktime(new Date());
        click.setDeviceType(deviceType);
        click.setShortUrl(shorturl);

        ShortnerUtil.getDefault().addClick(click);

        String redirectUrl = shorturl.getRealUrl().getUrl().replace("http//", "http://").replace("https//", "https://");
        if (!redirectUrl.toLowerCase().startsWith("http://") || !redirectUrl.toLowerCase().startsWith("https://")) {
            redirectUrl = "http://" + redirectUrl;
        }
        return "redirect:" + redirectUrl;
    }

    private String gen() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(0, 5);
    }

    private static boolean IsMatch(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(HttpServletRequest request, Model model) throws MalformedURLException, IOException {

        ShortnerEntities.build_DB();

        String surl = request.getParameter("surl");
        String rurl = request.getParameter("rurl");

        model.addAttribute("LongURL", rurl);
        model.addAttribute("ShortURL", surl);

        if (!"".equals(surl) && ShortnerUtil.getDefault().getShortUrl(surl) != null) {
            model.addAttribute("Error", "the short link already in use, Select another one");
            return "index";
        }
        
        String regex = "^((https?|ftp|file)://)?[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        
        if (!IsMatch(rurl, regex))
        {
            model.addAttribute("Error", "real URL is invalid.");
            return "index";
        }

        if ("".equals(surl)) {
            do {
                surl = gen();
            } while (ShortnerUtil.getDefault().getShortUrl(surl) != null);
        }

        model.addAttribute("LinkShortner", request.getLocalAddr() + request.getContextPath() + "/" + surl);
        model.addAttribute("LinkDataShortner", request.getLocalAddr() + request.getContextPath() + "/" + surl + "/data");
        model.addAttribute("Error", "");

        RealURL realurl = new RealURL();
        realurl.setUrl(rurl);

        realurl = ShortnerUtil.getDefault().addRealUrl(realurl);

        ShortURL shorturl = new ShortURL();
        shorturl.setUrl(surl);
        shorturl.setCreationtime(new Date());
        shorturl.setRealUrl(realurl);

        ShortnerUtil.getDefault().addShortUrl(shorturl);

        return "index";
    }

    @RequestMapping(value = "/{surl}/data", method = RequestMethod.GET)
    public String getData(HttpServletRequest request, HttpServletResponse response, Model model, @PathVariable final String surl) throws IOException, URISyntaxException {

        ShortnerEntities.build_DB();
        // query realURL
        ShortURL shorturl = ShortnerUtil.getDefault().getShortUrl(surl);

        Set<Click> clicks = shorturl.getClicks();
        if (shorturl == null) {
            response.setHeader("Location", surl + "/notFound");
        }
        // save XML file
//        String fullPath = "";
//        try {
//
//            fullPath = context.getRealPath("/WEB-INF/resources/xml");
//
//            File file = new File(fullPath + File.separator + shorturl.getUrl() + ".xml");
//            JAXBContext jaxbContext = JAXBContext.newInstance(ShortURL.class);
//            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//            // output pretty printed
//            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            jaxbMarshaller.marshal(shorturl, file);
//
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }

        String html = "<table class=\"table\">";
        html += "  <thead>\n"
                + "    <tr>\n"
                + "      <th class=\"row-1 row-ID\">ID</th>\n"
                + "      <th class=\"row-2 row-Time\">Time</th>\n"
                + "      <th class=\"row-3 row-IP\">IP</th>\n"
                + "      <th class=\"row-4 row-Device\">Device Type<th>\n"
                + "    </tr>\n"
                + "  </thead>"
                + "<tbody>";
        int id = 1;
        for (Click click : clicks) {
            html += "<tr>";
            html += "<td>" + id + "</td>";
            html += "<td>" + click.getClicktime().toString() + "</td>";
            html += "<td>" + click.getIP().toString() + "</td>";
            html += "<td>" + click.getDeviceType().toString() + "</td>";
            html += "</tr>";
            id++;
        }
        html += "</tbody>";
        html += "</table>";
        model.addAttribute("HtmlTable", html);
        return "data";
    }
}
