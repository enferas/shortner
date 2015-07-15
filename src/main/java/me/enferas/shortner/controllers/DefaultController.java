package me.enferas.shortner.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.enferas.entities.Click;
import me.enferas.entities.DeviceType;
import me.enferas.entities.ShortnerEntities;
import me.enferas.entities.ShortnerUtil;
import me.enferas.entities.RealURL;
import me.enferas.entities.ShortURL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

@Controller
public class DefaultController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {

        return "index";
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
            return "notfound";
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

        // redirect to realURL
        //response.setHeader("Location", shorturl.getRealUrl().getUrl());
        String redirectUrl = shorturl.getRealUrl().getUrl().replace("http//", "http://").replace("https//", "https://");
        if (!redirectUrl.toLowerCase().startsWith("http://") || !redirectUrl.toLowerCase().startsWith("https://")) {
            redirectUrl = "http://" + redirectUrl;
        }
        return "redirect:" + redirectUrl;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(HttpServletRequest request) throws MalformedURLException, IOException {
        String surl = request.getParameter("surl");
        String rurl = request.getParameter("rurl");

        // check if short exists
        ShortnerEntities.build_DB();
        RealURL realurl = new RealURL();
        realurl.setUrl(rurl);

        realurl = ShortnerUtil.getDefault().addRealUrl(realurl);

        ShortURL shorturl = new ShortURL();
        shorturl.setUrl(surl);
        shorturl.setCreationtime(new Date());
        shorturl.setRealUrl(realurl);

        ShortnerUtil.getDefault().addShortUrl(shorturl);

        return "redirect:/";
    }

    @RequestMapping(value = "/{surl}/data", method = RequestMethod.GET)
    public void getData(HttpServletRequest request, HttpServletResponse response, Device device, @PathVariable final String surl) throws IOException {

        if (surl == null || "".equals(surl)) {
            // return "index";
        }

        ShortnerEntities.build_DB();
        // query realURL
        ShortURL shorturl = ShortnerUtil.getDefault().getShortUrl(surl);

        Set<Click> clicks = shorturl.getClicks();
        if (shorturl == null) {
            // return "notfound";
        }

        // save XML file
        try {

            File file = new File("/resources/xml/" + shorturl.getUrl() + ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(ShortURL.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(shorturl, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

        try (PrintWriter out = response.getWriter()) {
            String html = "<html><head>\n"
                    + "        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                    + "        <title>Shortner</title>\n"
                    + "        <link type=\"text/css\" rel=\"stylesheet\" href=\"/shortner/resources/css/site.css\">\n"
                    + "        <script type=\"text/javascript\" src=\"/shortner/resources/js/script.js\"></script>\n"
                    + "    </head>\n"
                    + "    <body>\n"
                    + "<img src=\"/shortner/resources/images/images.jpg\" class=\"bg\">"
                    + "<div class=\"pos\">"
                    + "<div class=\"container-title otto\">  Shortner  </div>";

            html += "<table class=\"table\">";
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
            
            html += "<p class=\"text\"> <a href=\"/shortner/resources/xml/" + shorturl.getUrl() + ".xml\">Click Here To Show the Data in XML File</a> </p>";
            html += "</div></body></html>";

            out.println(html);
        }
        // return "";
    }
}
