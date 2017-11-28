package fr.mikaelbenes.timesheeter.controller;

import fr.mikaelbenes.timesheeter.TimesheeterApplication;
import fr.mikaelbenes.timesheeter.data.domain.Activity;
import fr.mikaelbenes.timesheeter.data.repository.ActivityRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith( SpringRunner.class )
@SpringBootTest( classes = TimesheeterApplication.class )
@WebAppConfiguration
public class ActivityRestControllerTest {

	private static final Logger logger				= LoggerFactory.getLogger( ActivityRestController.class );
	private static final String ENDPOINT_PATH		= "/activities";
	private static final Activity activityTest1 	= new Activity( "Test 1", "Redmine", "1234" );
	private static final Activity activityTest2 	= new Activity( "Test 2" );
	private static final Activity activityTest3		= new Activity( "Test 3", "Redmine", "12345" );
	private static final Activity activityTest4		= new Activity( "Test 4", "Redmine", "123" );

	private MediaType contentType = new MediaType(
			MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName( "utf8" )
	);

	private MockMvc mockMvc;
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	private List<Activity> activities = new ArrayList<>();

	@Autowired
	private ActivityRepository activityRepo;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	void setConverters( HttpMessageConverter<?>[] converters ) {
		this.mappingJackson2HttpMessageConverter = Arrays.asList( converters )
				.stream()
				.filter( hmc -> hmc instanceof MappingJackson2HttpMessageConverter )
				.findAny()
				.orElse( null );

		assertNotNull( "the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter );
	}

	@Before
	public void setUp() throws Exception {
		this.mockMvc = webAppContextSetup( webApplicationContext ).build();

		this.activityRepo.deleteAllInBatch();
		this.activities.clear();

		activityTest3.stop();
		Activity activityTest1 = this.activityRepo.save( this.activityTest1 );
		Activity activityTest2 = this.activityRepo.save( this.activityTest2 );
		Activity activityTest3 = this.activityRepo.save( this.activityTest3 );

		this.activities.add( activityTest1 );
		this.activities.add( activityTest2 );
		this.activities.add( activityTest3 );
	}

	@Test
	public void getActivities() throws Exception {
		this.mockMvc.perform( get(ENDPOINT_PATH) )
				.andExpect( status().isOk() )
				.andExpect( content().contentType(this.contentType) )
				.andExpect( jsonPath("$", hasSize(3)) )
				.andExpect( jsonPath("$[0].id", is(this.activities.get(0).getId().intValue())) )
				.andExpect( jsonPath("$[0].title", is(this.activities.get(0).getTitle())) )
				.andExpect( jsonPath("$[0].activityType", is(this.activities.get(0).getActivityType())) )
				.andExpect( jsonPath("$[0].activityTicket", is(this.activities.get(0).getActivityTicket())) )
				.andExpect( jsonPath("$[0].startTime", notNullValue()) )
				.andExpect( jsonPath("$[0].stopTime", nullValue()) )
				.andExpect( jsonPath("$[0].duration", isEmptyString()) )

				.andExpect( jsonPath("$[1].id", is(this.activities.get(1).getId().intValue())) )
				.andExpect( jsonPath("$[1].title", is(this.activities.get(1).getTitle())) )
				.andExpect( jsonPath("$[1].activityType", isEmptyOrNullString()) )
				.andExpect( jsonPath("$[1].activityTicket", isEmptyOrNullString()) )
				.andExpect( jsonPath("$[1].startTime", notNullValue()) )
				.andExpect( jsonPath("$[1].stopTime", nullValue()) )
				.andExpect( jsonPath("$[1].duration", isEmptyString()) )

				.andExpect( jsonPath("$[2].id", is(this.activities.get(2).getId().intValue())) )
				.andExpect( jsonPath("$[2].title", is(this.activities.get(2).getTitle())) )
				.andExpect( jsonPath("$[2].activityType", is(this.activities.get(2).getActivityType())) )
				.andExpect( jsonPath("$[2].activityTicket", is(this.activities.get(2).getActivityTicket())) )
				.andExpect( jsonPath("$[2].startTime", notNullValue()) )
				.andExpect( jsonPath("$[2].stopTime", notNullValue()) )
				.andExpect( jsonPath("$[2].duration", is(this.activities.get(2).getDuration())) );
	}

	@Test
	public void getActivity() throws Exception {
		this.mockMvc.perform( get(ENDPOINT_PATH + "/" + this.activities.get(0).getId()) )
				.andExpect( status().isOk() )
				.andExpect( content().contentType(this.contentType) )
				.andExpect( jsonPath("$.id", is(this.activities.get(0).getId().intValue())) )
				.andExpect( jsonPath("$.title", is(this.activities.get(0).getTitle())) )
				.andExpect( jsonPath("$.activityType", is(this.activities.get(0).getActivityType())) )
				.andExpect( jsonPath("$.activityTicket", is(this.activities.get(0).getActivityTicket())) )
				.andExpect( jsonPath("$.startTime", notNullValue()) )
				.andExpect( jsonPath("$.stopTime", nullValue()) )
				.andExpect( jsonPath("$.duration", isEmptyString()) );
	}

	@Test
	public void createActivity() throws Exception {
		String activityJson = this.json( this.activityTest4 );

		this.mockMvc.perform(
				post( ENDPOINT_PATH )
				.contentType( this.contentType )
				.content( activityJson )
		)
				.andExpect( status().isCreated() );
	}

	@Test
	public void duplicateActivity() throws Exception {
		this.mockMvc.perform( post(ENDPOINT_PATH + "/startFrom/" + this.activities.get(0).getId()) )
				.andExpect( status().isCreated() )
				.andExpect( content().contentType(this.contentType) )
				.andExpect( jsonPath("$.id", notNullValue()) )
				.andExpect( jsonPath("$.title", is(this.activities.get(0).getTitle())) )
				.andExpect( jsonPath("$.activityType", is(this.activities.get(0).getActivityType())) )
				.andExpect( jsonPath("$.activityTicket", is(this.activities.get(0).getActivityTicket())) )
				.andExpect( jsonPath("$.startTime", notNullValue()) )
				.andExpect( jsonPath("$.stopTime", nullValue()) )
				.andExpect( jsonPath("$.duration", isEmptyString()) );
	}

	@Test
	public void stopActivity() throws Exception {
		this.mockMvc.perform( post(ENDPOINT_PATH + "/" + this.activities.get(0).getId() + "/stop") )
				.andExpect( status().isOk() )
				.andExpect( content().contentType(this.contentType) )
				.andExpect( jsonPath("$.id", is(this.activities.get(0).getId().intValue())) )
				.andExpect( jsonPath("$.title", is(this.activities.get(0).getTitle())) )
				.andExpect( jsonPath("$.activityType", is(this.activities.get(0).getActivityType())) )
				.andExpect( jsonPath("$.activityTicket", is(this.activities.get(0).getActivityTicket())) )
				.andExpect( jsonPath("$.startTime", notNullValue()) )
				.andExpect( jsonPath("$.stopTime", notNullValue()) )
				.andExpect( jsonPath("$.duration", notNullValue()) );
	}

	@Test
	public void updateActivity() throws Exception {
		String activityJson = this.json( this.activityTest4 );

		this.mockMvc.perform(
				patch( ENDPOINT_PATH + "/" + this.activities.get(0).getId() )
						.contentType( this.contentType )
						.content( activityJson )
		)
				.andExpect( status().isOk() )
				.andExpect( content().contentType(this.contentType) )
				.andExpect( jsonPath("$.id", is(this.activities.get(0).getId().intValue())) )
				.andExpect( jsonPath("$.title", is(this.activityTest4.getTitle())) )
				.andExpect( jsonPath("$.activityType", is(this.activityTest4.getActivityType())) )
				.andExpect( jsonPath("$.activityTicket", is(this.activityTest4.getActivityTicket())) )
				.andExpect( jsonPath("$.startTime", notNullValue()) )
				.andExpect( jsonPath("$.stopTime", nullValue()) )
				.andExpect( jsonPath("$.duration", isEmptyString()) );
	}

	@Test
	public void deleteActivity() throws Exception {
		this.mockMvc.perform( delete(ENDPOINT_PATH + "/" + this.activities.get(0).getId()) )
				.andExpect( status().isNoContent() );
	}

	protected String json( Object o ) throws IOException {
		MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write( o, MediaType.APPLICATION_JSON, outputMessage );

		return outputMessage.getBodyAsString();
	}

}