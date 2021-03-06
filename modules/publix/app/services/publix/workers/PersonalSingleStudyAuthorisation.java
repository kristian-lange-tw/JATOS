package services.publix.workers;

import javax.inject.Inject;
import javax.inject.Singleton;

import exceptions.publix.ForbiddenPublixException;
import models.common.Batch;
import models.common.Study;
import models.common.StudyResult;
import models.common.StudyResult.StudyState;
import models.common.workers.PersonalSingleWorker;
import services.publix.PublixErrorMessages;
import services.publix.PublixHelpers;
import services.publix.StudyAuthorisation;

import java.util.Optional;

/**
 * PersonalSinglePublix's implementation of StudyAuthorization
 *
 * @author Kristian Lange
 */
@Singleton
public class PersonalSingleStudyAuthorisation extends StudyAuthorisation<PersonalSingleWorker> {

    @Inject
    PersonalSingleStudyAuthorisation(PersonalSingleErrorMessages errorMessages) {
    }

    @Override
    public void checkWorkerAllowedToStartStudy(PersonalSingleWorker worker, Study study,
            Batch batch) throws ForbiddenPublixException {
        if (!batch.isActive()) {
            throw new ForbiddenPublixException(PublixErrorMessages.batchInactive(batch.getId()));
        }
        // Personal Single Runs are used only once - don't start if worker has a
        // study result (although it is in state PRE)
        Optional<StudyResult> first = worker.getFirstStudyResult();
        if (first.isPresent() && first.get().getStudyState() != StudyResult.StudyState.PRE) {
            throw new ForbiddenPublixException(PublixErrorMessages.STUDY_CAN_BE_DONE_ONLY_ONCE);
        }
        checkMaxTotalWorkers(batch, worker);
        checkWorkerAllowedToDoStudy(worker, study, batch);
    }

    @Override
    public void checkWorkerAllowedToDoStudy(PersonalSingleWorker worker,
            Study study, Batch batch) throws ForbiddenPublixException {
        // Check if worker type is allowed
        if (!batch.hasAllowedWorkerType(worker.getWorkerType())) {
            throw new ForbiddenPublixException(PublixErrorMessages
                    .workerTypeNotAllowed(worker.getUIWorkerType(), study.getId(), batch.getId()));
        }
        // Personal single workers can't repeat the same study
        if (PublixHelpers.finishedStudyAlready(worker, study)) {
            throw new ForbiddenPublixException(PublixErrorMessages.STUDY_CAN_BE_DONE_ONLY_ONCE);
        }
    }

}
