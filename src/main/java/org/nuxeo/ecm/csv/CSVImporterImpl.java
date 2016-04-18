/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Thomas Roger
 */

package org.nuxeo.ecm.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * @since 5.7
 */
public class CSVImporterImpl implements CSVImporter {

    @Override
    public String launchImport(CoreSession session, String parentPath, File csvFile, String csvFileName,
            CSVImporterOptions options) {
        return new CSVImporterWork(session.getRepositoryName(), parentPath, session.getPrincipal()
                .getName(), csvFile,
                csvFileName, options).launch();
    }

    @Override
    public CSVImportStatus getImportStatus(String id) {
        return CSVImporterWork.getStatus(id);
    }

    @Override
    public List<CSVImportLog> getImportLogs(String id) {
        return getLastImportLogs(id, -1);
    }

    @Override
    public List<CSVImportLog> getImportLogs(String id, CSVImportLog.Status... status) {
        return getLastImportLogs(id, -1, status);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<CSVImportLog> getLastImportLogs(String id, int max) {
        List<CSVImportLog> importLogs = CSVImporterWork.getLastImportLogs(id);
        max = (max == -1 || max > importLogs.size()) ? importLogs.size() : max;
        return importLogs.subList(importLogs.size() - max, importLogs.size());
    }

    @Override
    public List<CSVImportLog> getLastImportLogs(String id, int max, CSVImportLog.Status... status) {
        List<CSVImportLog> importLogs = getLastImportLogs(id, max);
        return status.length == 0 ? importLogs : filterImportLogs(importLogs, status);
    }

    protected List<CSVImportLog> filterImportLogs(List<CSVImportLog> importLogs, CSVImportLog.Status... status) {
        List<CSVImportLog.Status> statusList = Arrays.asList(status);
        List<CSVImportLog> filteredLogs = new ArrayList<CSVImportLog>();
        for (CSVImportLog log : importLogs) {
            if (statusList.contains(log.getStatus())) {
                filteredLogs.add(log);
            }
        }
        return filteredLogs;
    }

    @Override
    public CSVImportResult getImportResult(String id) {
        return CSVImportResult.fromImportLogs(getLastImportLogs(id, -1));
    }

}
