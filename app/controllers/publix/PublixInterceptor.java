package controllers.publix;

import play.api.mvc.Cookie;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import com.fasterxml.jackson.core.JsonProcessingException;

import exceptions.PublixException;

/**
 * Interceptor for Publix: it intercepts requests for MechArg's public API and
 * forwards them to one of the implementations of the API. Right now two
 * implementations exists: MTPublix for studies originating from MTurk and
 * MAPublix for studies and components started from within MechArg's UI.
 * 
 * @author madsen
 */
public class PublixInterceptor extends Controller implements IPublix {

	private IPublix maPublix = new MAPublix();
	private IPublix mtPublix = new MTPublix();

	@Override
	@Transactional
	public Result startStudy(Long studyId) throws PublixException {
		if (isFromMechArg()) {
			return maPublix.startStudy(studyId);
		} else {
			return mtPublix.startStudy(studyId);
		}
	}

	@Override
	@Transactional
	public Result startComponent(Long studyId, Long componentId)
			throws PublixException {
		if (isFromMechArg()) {
			return maPublix.startComponent(studyId, componentId);
		} else {
			return mtPublix.startComponent(studyId, componentId);
		}
	}

	@Override
	@Transactional
	public Result startNextComponent(Long studyId) throws PublixException {
		if (isFromMechArg()) {
			return maPublix.startNextComponent(studyId);
		} else {
			return mtPublix.startNextComponent(studyId);
		}
	}

	@Override
	@Transactional
	public Result getStudyData(Long studyId) throws PublixException,
			JsonProcessingException {
		if (isFromMechArg()) {
			return maPublix.getStudyData(studyId);
		} else {
			return mtPublix.getStudyData(studyId);
		}
	}

	@Override
	@Transactional
	public Result getComponentData(Long studyId, Long componentId)
			throws PublixException, JsonProcessingException {
		if (isFromMechArg()) {
			return maPublix.getComponentData(studyId, componentId);
		} else {
			return mtPublix.getComponentData(studyId, componentId);
		}
	}

	@Override
	@Transactional
	public Result submitResultData(Long studyId, Long componentId)
			throws PublixException {
		if (isFromMechArg()) {
			return maPublix.submitResultData(studyId, componentId);
		} else {
			return mtPublix.submitResultData(studyId, componentId);
		}
	}

	@Override
	@Transactional
	public Result finishStudy(Long studyId, Boolean successful, String errorMsg)
			throws PublixException {
		if (isFromMechArg()) {
			return maPublix.finishStudy(studyId, successful, errorMsg);
		} else {
			return mtPublix.finishStudy(studyId, successful, errorMsg);
		}
	}

	@Override
	public Result logError() {
		if (isFromMechArg()) {
			return maPublix.logError();
		} else {
			return mtPublix.logError();
		}
	}

	@Override
	public Result teapot() {
		if (isFromMechArg()) {
			return maPublix.teapot();
		} else {
			return mtPublix.teapot();
		}
	}

	/**
	 * Check if this request originates from within MechArg.
	 */
	private boolean isFromMechArg() {
		Http.Cookie playCookie = request().cookie("PLAY_SESSION");
		if (playCookie != null
				&& playCookie.value().contains(MAPublix.MECHARG_TRY)) {
			return true;
		}
		return false;
	}

}