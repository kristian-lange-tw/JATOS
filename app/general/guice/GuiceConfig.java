package general.guice;

import general.Initializer;
import general.OnStartStop;
import general.common.Common;
import groupservices.publix.akka.actors.GroupDispatcherRegistry;
import models.common.workers.GeneralSingleWorker;
import models.common.workers.JatosWorker;
import models.common.workers.MTWorker;
import models.common.workers.PersonalMultipleWorker;
import models.common.workers.PersonalSingleWorker;
import play.libs.akka.AkkaGuiceSupport;
import services.publix.IStudyAuthorisation;
import services.publix.PublixUtils;
import services.publix.general_single.GeneralSinglePublixUtils;
import services.publix.general_single.GeneralSingleStudyAuthorisation;
import services.publix.jatos.JatosPublixUtils;
import services.publix.jatos.JatosStudyAuthorisation;
import services.publix.mt.MTPublixUtils;
import services.publix.mt.MTStudyAuthorisation;
import services.publix.personal_multiple.PersonalMultiplePublixUtils;
import services.publix.personal_multiple.PersonalMultipleStudyAuthorisation;
import services.publix.personal_single.PersonalSinglePublixUtils;
import services.publix.personal_single.PersonalSingleStudyAuthorisation;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Initial configuration of Guice dependency injection
 * 
 * @author Kristian Lange (2015)
 */
public class GuiceConfig extends AbstractModule implements AkkaGuiceSupport {

	@Override
	protected void configure() {
		// JATOS startup initialisation (eager -> called during JATOS start)
		bind(Common.class).asEagerSingleton();
		bind(Initializer.class).asEagerSingleton();
		bind(OnStartStop.class).asEagerSingleton();

		// Config Worker generics binding for IStudyAuthorisation
		bind(new TypeLiteral<IStudyAuthorisation<GeneralSingleWorker>>() {
		}).to(GeneralSingleStudyAuthorisation.class);
		bind(new TypeLiteral<IStudyAuthorisation<JatosWorker>>() {
		}).to(JatosStudyAuthorisation.class);
		bind(new TypeLiteral<IStudyAuthorisation<MTWorker>>() {
		}).to(MTStudyAuthorisation.class);
		bind(new TypeLiteral<IStudyAuthorisation<PersonalMultipleWorker>>() {
		}).to(PersonalMultipleStudyAuthorisation.class);
		bind(new TypeLiteral<IStudyAuthorisation<PersonalSingleWorker>>() {
		}).to(PersonalSingleStudyAuthorisation.class);

		// Config Worker generics binding for PublixUtils
		bind(new TypeLiteral<PublixUtils<GeneralSingleWorker>>() {
		}).to(GeneralSinglePublixUtils.class);
		bind(new TypeLiteral<PublixUtils<JatosWorker>>() {
		}).to(JatosPublixUtils.class);
		bind(new TypeLiteral<PublixUtils<MTWorker>>() {
		}).to(MTPublixUtils.class);
		bind(new TypeLiteral<PublixUtils<PersonalMultipleWorker>>() {
		}).to(PersonalMultiplePublixUtils.class);
		bind(new TypeLiteral<PublixUtils<PersonalSingleWorker>>() {
		}).to(PersonalSinglePublixUtils.class);

		// Config which Akka actors should be handled by Guice
		bindActor(GroupDispatcherRegistry.class, "group-dispatcher-registry-actor");
	}

}
