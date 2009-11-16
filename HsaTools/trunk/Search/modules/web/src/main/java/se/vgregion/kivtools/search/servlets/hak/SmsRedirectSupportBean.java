package se.vgregion.kivtools.search.servlets.hak;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import se.vgregion.kivtools.search.sms.SmsRedirectService;

/**
 * Support bean for redirection to a page for sending SMS.
 *
 * @author David Bennehult & Joakim Olsson
 */
@Controller
public class SmsRedirectSupportBean {

  @Qualifier("smsRedirectService")
  private SmsRedirectService smsRedirectService;

  @Autowired
  public void setSmsRedirectService(SmsRedirectService smsRedirectService) {
    this.smsRedirectService = smsRedirectService;
  }

  /**
   * Performs a redirect to the page returned by the SmsRedirectService for the mobile number in the request.
   *
   * @param mobileNumber The mobile number to use for fetch url.
   * @return a Spring redirect-view to the url returned by the SmsRedirectService.
   */
  @RequestMapping("/sms/send.servlet")
  public String sendSms(@RequestParam("mobileNumber") String mobileNumber) {
    String url = smsRedirectService.retrieveSmsRedirectUrl(mobileNumber);
    return "redirect:" + url;
  }
}
