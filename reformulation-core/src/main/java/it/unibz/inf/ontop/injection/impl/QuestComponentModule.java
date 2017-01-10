package it.unibz.inf.ontop.injection.impl;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import it.unibz.inf.ontop.owlrefplatform.core.DBConnector;
import it.unibz.inf.ontop.owlrefplatform.core.IQuest;
import it.unibz.inf.ontop.owlrefplatform.core.QueryCache;
import it.unibz.inf.ontop.owlrefplatform.core.mappingprocessing.TMappingExclusionConfig;
import it.unibz.inf.ontop.owlrefplatform.core.srcquerygeneration.NativeQueryGenerator;
import it.unibz.inf.ontop.owlrefplatform.core.translator.MappingVocabularyFixer;
import it.unibz.inf.ontop.injection.QuestComponentFactory;
import it.unibz.inf.ontop.injection.QuestCoreConfiguration;
import it.unibz.inf.ontop.injection.QuestCoreSettings;

/**
 * TODO: describe
 */
public class QuestComponentModule extends OntopAbstractModule {

    // Temporary
    private QuestCoreConfiguration configuration;

    protected QuestComponentModule(QuestCoreConfiguration configuration) {
        super(configuration.getSettings());
        this.configuration = configuration;
    }

    @Override
    protected void configureCoreConfiguration() {
        super.configureCoreConfiguration();
        bind(QuestCoreSettings.class).toInstance((QuestCoreSettings) getProperties());
    }

    @Override
    protected void configure() {
        configureCoreConfiguration();

        bindTMappingExclusionConfig();

        Module componentFactoryModule = buildFactory(ImmutableList.<Class>of(IQuest.class,
                        NativeQueryGenerator.class, DBConnector.class),
                QuestComponentFactory.class);
        install(componentFactoryModule);
        bindFromPreferences(MappingVocabularyFixer.class);
        bindFromPreferences(QueryCache.class);
    }

    private void bindTMappingExclusionConfig() {
        TMappingExclusionConfig tMappingExclusionConfig = configuration.getTmappingExclusions()
                .orElseGet(TMappingExclusionConfig::empty);

        bind(TMappingExclusionConfig.class).toInstance(tMappingExclusionConfig);
    }
}
