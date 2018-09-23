package general

import javax.inject.Inject
import org.pac4j.play.filters.SecurityFilter
import play.api.http.HttpFilters

/**
  * Increases security
  * (https://www.playframework.com/documentation/2.5.x/SecurityHeaders)
  */
class Filters @Inject()(securityFilter: SecurityFilter) extends HttpFilters {

  def filters = Seq(securityFilter)

}
