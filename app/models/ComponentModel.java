package models;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.GenericGenerator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import play.data.validation.ValidationError;
import play.db.jpa.JPA;
import services.ErrorMessages;
import services.JsonUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Domain model and DAO of a component.
 * 
 * @author Kristian Lange
 */
@Entity
public class ComponentModel {

	public static final String TITLE = "title";
	public static final String HTML_FILE_PATH = "htmlFilePath";
	public static final String JSON_DATA = "jsonData";
	public static final String RESULT = "result";
	public static final String RELOADABLE = "reloadable";
	public static final String COMMENTS = "comments";
	public static final String COMPONENT = "component";

	@Id
	@GeneratedValue
	@JsonView(JsonUtils.JsonForPublix.class)
	private Long id;

	/**
	 * Universally unique ID. Used for import/export between different JATOS
	 * instances.
	 */
	@JsonView(JsonUtils.JsonForIO.class)
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private String uuid;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "study_id")
	private StudyModel study;

	@JsonView({ JsonUtils.JsonForPublix.class, JsonUtils.JsonForIO.class })
	private String title;

	/**
	 * Timestamp of the creation or the last update of this component
	 */
	@JsonIgnore
	private Timestamp date;

	/**
	 * Local path to component's HTML file in the study's directory. File
	 * separators are persisted as '/'.
	 */
	@JsonView({ JsonUtils.JsonForPublix.class, JsonUtils.JsonForIO.class })
	@JoinColumn(name = "viewUrl")
	private String htmlFilePath;

	@JsonView({ JsonUtils.JsonForPublix.class, JsonUtils.JsonForIO.class })
	private boolean reloadable = false;

	/**
	 * An inactive component can't be used within a study - it generates an
	 * error message if one try. Further it's skipped if one uses
	 * startNextComponent from the public API.
	 */
	@JsonView(JsonUtils.JsonForIO.class)
	private boolean active = true;

	/**
	 * User comments, reminders, something to share with others. They have no
	 * further meaning.
	 */
	@Lob
	@JsonView({ JsonUtils.JsonForIO.class })
	private String comments;

	@JsonView({ JsonUtils.JsonForPublix.class, JsonUtils.JsonForIO.class })
	@Lob
	private String jsonData;

	public ComponentModel() {
	}

	/**
	 * Constructor for cloning
	 */
	public ComponentModel(ComponentModel component) {
		this.study = component.study;
		this.title = component.title;
		this.htmlFilePath = component.htmlFilePath;
		this.reloadable = component.reloadable;
		this.active = component.active;
		this.jsonData = component.jsonData;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public void setStudy(StudyModel study) {
		this.study = study;
	}

	public StudyModel getStudy() {
		return this.study;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Timestamp getDate() {
		return this.date;
	}

	public void setHtmlFilePath(String htmlFilePath) {
		this.htmlFilePath = htmlFilePath;
	}

	public String getHtmlFilePath() {
		if (htmlFilePath != null) {
			return this.htmlFilePath.replace('/', File.separatorChar);
		} else {
			return null;
		}
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return this.comments;
	}

	public String getJsonData() {
		if (this.jsonData == null) {
			return null;
		}
		return JsonUtils.makePretty(jsonData);
	}

	public void setJsonData(String jsonDataStr) {
		if (jsonDataStr == null) {
			this.jsonData = null;
			return;
		}
		if (!JsonUtils.isValidJSON(jsonDataStr)) {
			// Set the invalid string anyway, but don't standardise it. It will
			// cause an error during next validate() if one tries to edit this
			// component.
			this.jsonData = jsonDataStr;
			return;
		}
		this.jsonData = JsonUtils.asStringForDB(jsonDataStr);
	}

	public boolean isReloadable() {
		return reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<ValidationError> validate() {
		List<ValidationError> errorList = new ArrayList<ValidationError>();
		if (title == null || title.isEmpty()) {
			errorList.add(new ValidationError(TITLE,
					ErrorMessages.MISSING_TITLE));
		}
		if (title != null && !Jsoup.isValid(title, Whitelist.none())) {
			errorList.add(new ValidationError(TITLE,
					ErrorMessages.NO_HTML_ALLOWED));
		}
		if (htmlFilePath != null && !htmlFilePath.isEmpty()) {
			String pathRegEx = "^(\\w+)(\\/\\w+)?\\.\\w+(\\?(\\w+=[\\w\\d]+(&\\w+=[\\w\\d]+)+)+)*$";
			if (!(htmlFilePath.matches(pathRegEx) || htmlFilePath.isEmpty())) {
				errorList.add(new ValidationError(HTML_FILE_PATH,
						ErrorMessages.NOT_A_VALID_PATH_YOU_CAN_LEAVE_IT_EMPTY));
			}
		}
		if (comments != null && !Jsoup.isValid(comments, Whitelist.none())) {
			errorList.add(new ValidationError(COMMENTS,
					ErrorMessages.NO_HTML_ALLOWED));
		}
		if (jsonData != null && !JsonUtils.isValidJSON(jsonData)) {
			errorList.add(new ValidationError(JSON_DATA,
					ErrorMessages.INVALID_JSON_FORMAT));
		}
		return errorList.isEmpty() ? null : errorList;
	}

	@Override
	public String toString() {
		return id + " " + title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ComponentModel)) {
			return false;
		}
		ComponentModel other = (ComponentModel) obj;
		if (id == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!id.equals(other.getId())) {
			return false;
		}
		return true;
	}

	public static ComponentModel findById(Long id) {
		return JPA.em().find(ComponentModel.class, id);
	}

	public static ComponentModel findByUuid(String uuid) {
		String queryStr = "SELECT e FROM ComponentModel e WHERE "
				+ "e.uuid=:uuid";
		TypedQuery<ComponentModel> query = JPA.em().createQuery(queryStr,
				ComponentModel.class);
		List<ComponentModel> studyList = query.setParameter("uuid", uuid)
				.getResultList();
		ComponentModel study = studyList.isEmpty() ? null
				: (ComponentModel) studyList.get(0);
		return study;
	}

	public static List<ComponentModel> findAll() {
		TypedQuery<ComponentModel> query = JPA.em().createQuery(
				"SELECT e FROM ComponentModel e", ComponentModel.class);
		return query.getResultList();
	}

	public static void changeComponentOrder(ComponentModel component,
			int newIndex) {
		String queryStr = "UPDATE ComponentModel SET componentList_order = "
				+ ":newIndex WHERE id = :id";
		Query query = JPA.em().createQuery(queryStr);
		query.setParameter("newIndex", newIndex);
		query.setParameter("id", component.id);
		query.executeUpdate();
	}

	public void persist() {
		JPA.em().persist(this);
	}

	public void merge() {
		JPA.em().merge(this);
	}

	public void remove() {
		JPA.em().remove(this);
	}

}